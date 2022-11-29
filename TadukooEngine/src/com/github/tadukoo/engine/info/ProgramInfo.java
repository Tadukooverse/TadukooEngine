package com.github.tadukoo.engine.info;

import com.github.tadukoo.engine.Program;
import com.github.tadukoo.engine.ProgramHandler;
import com.github.tadukoo.parsing.json.JSONArrayList;
import com.github.tadukoo.parsing.json.JSONClass;
import com.github.tadukoo.parsing.json.OrderedJSONClass;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.StringUtil;
import com.github.tadukoo.util.logger.EasyLogger;
import com.github.tadukoo.util.map.MapUtil;
import com.github.tadukoo.util.pojo.MappedPojo;
import com.github.tadukoo.util.tuple.Pair;
import com.github.tadukoo.view.form.AbstractSimpleForm;
import com.github.tadukoo.view.form.Form;
import com.github.tadukoo.view.form.field.ButtonFormField;
import com.github.tadukoo.view.form.field.LabelType;
import com.github.tadukoo.view.form.field.StringFormField;

import java.util.ArrayList;
import java.util.List;

/**
 * Program Info contains information needed for a {@link Program}, including title, description, and
 * information about jar files used.
 * <br><br>
 * It can be converted to/from a JSON file and this class can be used as a {@link Form} for showing
 * Program info and launching it.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public class ProgramInfo extends AbstractSimpleForm implements OrderedJSONClass{
	
	/**
	 * A builder used to create a {@link ProgramInfo} object. It takes the following parameters:
	 *
	 * <table>
	 *     <caption>Program Info Parameters</caption>
	 *     <tr>
	 *         <th>Parameter</th>
	 *         <th>Description</th>
	 *         <th>Default or Required</th>
	 *     </tr>
	 *     <tr>
	 *         <td>logger</td>
	 *         <td>An {@link EasyLogger} to use for logging if needed</td>
	 *         <td>Defaults to {@code null}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>title</td>
	 *         <td>The title of the Program</td>
	 *         <td>Required</td>
	 *     </tr>
	 *     <tr>
	 *         <td>description</td>
	 *         <td>The description of the Program</td>
	 *         <td>Required</td>
	 *     </tr>
	 *     <tr>
	 *         <td>programJarName</td>
	 *         <td>The name for the Program's jar file</td>
	 *         <td>Defaults to {@code title} (with spaces removed) .jar</td>
	 *     </tr>
	 *     <tr>
	 *         <td>libraries</td>
	 *         <td>A List of {@link ShortInfo}s for libraries the Program uses</td>
	 *         <td>Defaults to an empty list</td>
	 *     </tr>
	 *     <tr>
	 *         <td>programHandler</td>
	 *         <td>The {@link ProgramHandler} to be used</td>
	 *         <td>Required if the {@link ProgramInfo} is used as a form, otherwise defaults to null</td>
	 *     </tr>
	 * </table>
	 *
	 * @author Logan Ferree (Tadukoo)
	 * @version Alpha v.0.1
	 */
	public static class ProgramInfoBuilder{
		/** The {@link EasyLogger} to use for logging */
		private EasyLogger logger = null;
		/** The Title of the Program */
		private String title;
		/** The Description of the Program */
		private String description;
		/** The name for the Program's jar file */
		private String programJarName = null;
		/** The {@link ShortInfo}s of the libraries the Program uses */
		private JSONArrayList<ShortInfo> libraries = new JSONArrayList<>();
		/** The {@link ProgramHandler} to be used */
		private ProgramHandler programHandler = null;
		
		/** Not allowed to create a ProgramInfoBuilder outside ProgramInfo */
		private ProgramInfoBuilder(){ }
		
		/**
		 * @param logger The {@link EasyLogger} to use for logging if needed
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder logger(EasyLogger logger){
			this.logger = logger;
			return this;
		}
		
		/**
		 * @param title The Title of the Program
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder title(String title){
			this.title = title;
			return this;
		}
		
		/**
		 * @param description The Description of the Program
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder description(String description){
			this.description = description;
			return this;
		}
		
		/**
		 * @param programJarName The name for the Program's jar file
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder programJarName(String programJarName){
			this.programJarName = programJarName;
			return this;
		}
		
		/**
		 * @param libraries The {@link ShortInfo}s of the libraries the Program uses
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder libraries(JSONArrayList<ShortInfo> libraries){
			this.libraries = libraries;
			return this;
		}
		
		/**
		 * @param libraries The {@link ShortInfo}s of the libraries the Program uses
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder libraries(List<ShortInfo> libraries){
			this.libraries = new JSONArrayList<>(libraries);
			return this;
		}
		
		/**
		 * @param library The {@link ShortInfo} of a library the Program uses (to be added to the list)
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder library(ShortInfo library){
			libraries.add(library);
			return this;
		}
		
		/**
		 * Creates a new Library {@link ShortInfo} for a library the Program uses and adds it to the
		 * list of libraries
		 *
		 * @param title The title of the library
		 * @param filename The filename of the library
		 * @param fileLocation The location of the file for the library (to be downloaded from)
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder library(String title, String filename, String fileLocation){
			libraries.add(new ShortInfo(InfoType.LIB, title, filename, fileLocation));
			return this;
		}
		
		/**
		 * @param programHandler The {@link ProgramHandler} to be used
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder programHandler(ProgramHandler programHandler){
			this.programHandler = programHandler;
			return this;
		}
		
		/**
		 * Checks for errors in the {@link ProgramInfo} parameters and throws an exception if any are found
		 *
		 * @throws IllegalArgumentException If there are problems with the parameters set
		 */
		private void checkForErrors(){
			List<String> errors = new ArrayList<>();
			
			// Title is required
			if(StringUtil.isBlank(title)){
				errors.add("title is required");
			}
			// Description is required
			if(StringUtil.isBlank(description)){
				errors.add("description is required");
			}
			
			// If we find errors, throw an exception
			if(ListUtil.isNotBlank(errors)){
				throw new IllegalArgumentException("The following errors happened in building a ProgramInfo: \n"
						+ StringUtil.buildStringWithNewLines(errors));
			}
		}
		
		/**
		 * Builds a new {@link ProgramInfo} and returns it
		 *
		 * @return The newly built {@link ProgramInfo}
		 * @throws Throwable If anything goes wrong
		 */
		public ProgramInfo build() throws Throwable{
			checkForErrors();
			
			// Determine jar file name (if it's not perfect)
			if(StringUtil.isBlank(programJarName)){
				programJarName = title.replaceAll(" ", "") + ".jar";
			}else if(!programJarName.endsWith(".jar")){
				// Add .jar if it's missing
				programJarName += ".jar";
			}
			
			// Build the ProgramInfo
			return new ProgramInfo(logger, title, description,
					programJarName, libraries,
					programHandler);
		}
	}
	
	/** Key used for the {@link EasyLogger} */
	private static final String LOGGER = "logger";
	/** Key used for the Title of the Program */
	private static final String TITLE = "title";
	/** Key used for the Description of the Program */
	private static final String DESCRIPTION = "description";
	/** Key used for the Program Jar Name of the Program */
	private static final String PROGRAM_JAR_NAME = "program-jar-name";
	/** Key used for the Library {@link ShortInfo}s of the Program */
	private static final String LIBRARIES = "libraries";
	/** Key used for the {@link ProgramHandler} to be used */
	public static final String PROGRAM_HANDLER = "Program Handler";
	
	/**
	 * Creates a new ProgramInfo programmatically with the given information.
	 *
	 * @param logger An {@link EasyLogger} to use for logging if needed
	 * @param title The title of the Program
	 * @param description The description for the Program
	 * @param programJarName The name for the Program's jar file
	 * @param libraries The {@link ShortInfo}s of the libraries the Program uses
	 * @param programHandler The {@link ProgramHandler} to be used
	 * @throws Throwable If anything goes wrong
	 */
	private ProgramInfo(
			EasyLogger logger, String title, String description,
			String programJarName, JSONArrayList<ShortInfo> libraries,
			ProgramHandler programHandler) throws Throwable{
		super(MapUtil.createMap(Pair.of(LOGGER, logger), Pair.of(TITLE, title), Pair.of(DESCRIPTION, description),
				Pair.of(PROGRAM_JAR_NAME, programJarName), Pair.of(LIBRARIES, libraries),
				Pair.of(PROGRAM_HANDLER, programHandler)));
	}
	
	/**
	 * Creates a new ProgramInfo using a {@link MappedPojo} for its values.
	 * This is used by {@link JSONClass} for parsing purposes.
	 *
	 * @param pojo The {@link MappedPojo} to use for values
	 * @throws Throwable If anything goes wrong
	 */
	public ProgramInfo(MappedPojo pojo) throws Throwable{
		super(pojo);
	}
	
	/**
	 * @return A {@link ProgramInfoBuilder} to use to build a {@link ProgramInfo}
	 */
	public static ProgramInfoBuilder builder(){
		return new ProgramInfoBuilder();
	}
	
	/** {@inheritDoc} */
	@Override
	public List<String> getKeyOrder(){
		return ListUtil.createList(TITLE, DESCRIPTION, PROGRAM_JAR_NAME, LIBRARIES);
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDefaultFields(){
		// Grab the title + description from the map
		String title = getTitle();
		String description = getDescription();
		
		// Grab the ProgramHandler to be used for launching
		ProgramHandler programHandler = (ProgramHandler) getItem(PROGRAM_HANDLER);
		
		// Title field
		addField(StringFormField.builder()
				.key(TITLE).defaultValue(title)
				.labelType(LabelType.NONE)
				.rowPos(0).colPos(0)
				.stringFieldType(StringFormField.StringFieldType.TITLE)
				.build());
		
		// Description field
		addField(StringFormField.builder()
				.key(DESCRIPTION).defaultValue(description)
				.labelType(LabelType.NONE)
				.rowPos(1).colPos(0)
				.editable(false)
				.build());
		
		// Launch Button field
		addField(ButtonFormField.builder()
				.key("Launch")
				.rowPos(2).colPos(0)
				.actionListener(e -> programHandler.launchProgram(this))
				.build());
	}
	
	/**
	 * @return The {@link EasyLogger} to use for logging if needed
	 */
	public EasyLogger getLogger(){
		return (EasyLogger) getItem(LOGGER);
	}
	
	/**
	 * Sets the {@link EasyLogger} to use for logging
	 *
	 * @param logger The {@link EasyLogger} to use for logging
	 */
	public void setLogger(EasyLogger logger){
		setItem(LOGGER, logger);
	}
	
	/**
	 * @return The Title of this Program
	 */
	public String getTitle(){
		return (String) getItem(TITLE);
	}
	
	/**
	 * @return The Description of this Program
	 */
	public String getDescription(){
		return (String) getItem(DESCRIPTION);
	}
	
	/**
	 * @return The name for this Program's jar file
	 */
	public String getProgramJarName(){
		return (String) getItem(PROGRAM_JAR_NAME);
	}
	
	/**
	 * @return The {@link ShortInfo}s of the libraries the Program uses
	 */
	public List<ShortInfo> getLibraries(){
		return getJSONArrayItemNoThrow(getLogger(), LIBRARIES, ShortInfo.class);
	}
}

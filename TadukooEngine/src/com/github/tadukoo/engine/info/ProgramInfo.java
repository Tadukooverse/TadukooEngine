package com.github.tadukoo.engine.info;

import com.github.tadukoo.engine.Program;
import com.github.tadukoo.engine.ProgramHandler;
import com.github.tadukoo.parsing.json.JSONArrayList;
import com.github.tadukoo.parsing.json.JSONClass;
import com.github.tadukoo.parsing.json.OrderedJSONClass;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.StringUtil;
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
	 *         <td>libJarNames</td>
	 *         <td>The names of the library jar files the Program uses</td>
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
		/** The Title of the Program */
		private String title;
		/** The Description of the Program */
		private String description;
		/** The name for the Program's jar file */
		private String programJarName = null;
		/** The names of the library jar files the Program uses */
		private JSONArrayList<String> libJarNames = new JSONArrayList<>();
		/** The {@link ProgramHandler} to be used */
		private ProgramHandler programHandler = null;
		
		// Not allowed to create a ProgramInfoBuilder outside of ProgramInfo
		private ProgramInfoBuilder(){ }
		
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
		 * @param libJarNames The names of the library jar files the Program uses
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder libJarNames(JSONArrayList<String> libJarNames){
			this.libJarNames = libJarNames;
			return this;
		}
		
		/**
		 * @param libJarNames The names of the library jar files the Program uses
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder libJarNames(List<String> libJarNames){
			this.libJarNames = new JSONArrayList<>(libJarNames);
			return this;
		}
		
		/**
		 * @param libJarName The name of a library jar file the Program uses (to be added to the list)
		 * @return this, to continue building
		 */
		public ProgramInfoBuilder libJarName(String libJarName){
			libJarNames.add(libJarName);
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
			return new ProgramInfo(title, description,
					programJarName, libJarNames,
					programHandler);
		}
	}
	
	/** Key used for the Title of the Program */
	private static final String TITLE = "title";
	/** Key used for the Description of the Program */
	private static final String DESCRIPTION = "description";
	/** Key used for the Program Jar Name of the Program */
	private static final String PROGRAM_JAR_NAME = "program-jar-name";
	/** Key used for the Library Jar Names of the Program */
	private static final String LIB_JAR_NAMES = "library-jar-names";
	/** Key used for the {@link ProgramHandler} to be used */
	public static final String PROGRAM_HANDLER = "Program Handler";
	
	/**
	 * Creates a new ProgramInfo programmatically with the given information.
	 *
	 * @param title The title of the Program
	 * @param description The description for the Program
	 * @param programJarName The name for the Program's jar file
	 * @param libJarNames The names of the library jar files the Program uses
	 * @param programHandler The {@link ProgramHandler} to be used
	 * @throws Throwable If anything goes wrong
	 */
	private ProgramInfo(String title, String description,
	                    String programJarName, JSONArrayList<String> libJarNames,
	                    ProgramHandler programHandler) throws Throwable{
		super(MapUtil.createMap(Pair.of(TITLE, title), Pair.of(DESCRIPTION, description),
				Pair.of(PROGRAM_JAR_NAME, programJarName), Pair.of(LIB_JAR_NAMES, libJarNames),
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
		return ListUtil.createList(TITLE, DESCRIPTION, PROGRAM_JAR_NAME, LIB_JAR_NAMES);
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
	 * @return The names of the library jar files the Program uses
	 */
	@SuppressWarnings("unchecked")
	public List<String> getLibJarNames(){
		return (JSONArrayList<String>) getItem(LIB_JAR_NAMES);
	}
}

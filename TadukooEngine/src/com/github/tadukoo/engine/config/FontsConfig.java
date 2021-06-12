package com.github.tadukoo.engine.config;

import com.github.tadukoo.github.pojo.GitHubRelease;
import com.github.tadukoo.github.pojo.GitHubReleaseAsset;
import com.github.tadukoo.github.releases.LatestReleaseEndpoint;
import com.github.tadukoo.parsing.json.OrderedJSONClass;
import com.github.tadukoo.util.FileUtil;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.StringUtil;
import com.github.tadukoo.util.logger.EasyLogger;
import com.github.tadukoo.util.map.MapUtil;
import com.github.tadukoo.util.pojo.MappedPojo;
import com.github.tadukoo.util.tuple.Pair;
import com.github.tadukoo.view.font.FontFamilies;
import com.github.tadukoo.view.font.FontResourceLoader;
import com.github.tadukoo.view.form.AbstractSimpleForm;
import com.github.tadukoo.view.form.components.FileDownloader;
import com.github.tadukoo.view.form.field.ButtonFormField;
import com.github.tadukoo.view.form.field.StringFormField;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Fonts Config is used for fonts settings and to manually update the fonts
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public class FontsConfig extends AbstractSimpleForm implements OrderedJSONClass{
	
	/**
	 * Fonts Config Builder is used to build a new {@link FontsConfig}. It has the following parameters:
	 *
	 * <table>
	 *     <caption>Fonts Config Parameters</caption>
	 *     <tr>
	 *         <th>Parameter</th>
	 *         <th>Description</th>
	 *         <th>Default or Required</th>
	 *     </tr>
	 *     <tr>
	 *         <td>logger</td>
	 *         <td>The {@link EasyLogger} to used for logging</td>
	 *         <td>Required</td>
	 *     </tr>
	 *     <tr>
	 *         <td>fontsFolder</td>
	 *         <td>The folder to look for/store fonts</td>
	 *         <td>Required</td>
	 *     </tr>
	 * </table>
	 *
	 * @author Logan Ferree (Tadukoo)
	 * @version Alpha v.0.1
	 */
	public static class FontsConfigBuilder{
		/** The {@link EasyLogger} to use for logging */
		private EasyLogger logger = null;
		/** The folder to look for/store fonts */
		private String fontsFolder;
		
		// Not allowed to create Fonts Config Builder outside of Fonts Config
		private FontsConfigBuilder(){ }
		
		/**
		 * @param logger The {@link EasyLogger} to use for logging
		 * @return this, to continue building
		 */
		public FontsConfigBuilder logger(EasyLogger logger){
			this.logger = logger;
			return this;
		}
		
		/**
		 * @param fontsFolder The folder to look for/store fonts
		 * @return this, to continue building
		 */
		public FontsConfigBuilder fontsFolder(String fontsFolder){
			this.fontsFolder = fontsFolder;
			return this;
		}
		
		/**
		 * Builds a new {@link FontsConfig} with the set parameters
		 *
		 * @return The newly built {@link FontsConfig}
		 */
		public FontsConfig build() throws Throwable{
			return new FontsConfig(logger, fontsFolder);
		}
	}
	
	/** The key to use for the fonts folder */
	private static final String FONTS_FOLDER = "fonts-folder";
	/** The key to use for the {@link EasyLogger} */
	private static final String LOGGER = "logger";
	
	/**
	 * Creates a new Fonts Config with the given parameters
	 *
	 * @param logger The {@link EasyLogger} to use for logging
	 * @param fontsFolder The folder to look for/store fonts
	 * @throws Throwable If anything goes wrong in creating components
	 */
	private FontsConfig(EasyLogger logger, String fontsFolder) throws Throwable{
		super(MapUtil.createMap(Pair.of(LOGGER, logger), Pair.of(FONTS_FOLDER, fontsFolder)));
	}
	
	/**
	 * Creates a new Fonts Config object using a {@link MappedPojo} for the values
	 *
	 * @param pojo The pojo containing a map, to be used for default values for forms
	 *             that need them during {@link #setDefaultFields()}
	 * @throws Throwable If anything goes wrong in creating components
	 */
	public FontsConfig(MappedPojo pojo) throws Throwable{
		super(pojo);
	}
	
	/**
	 * @return A new {@link FontsConfigBuilder} to use to build a {@link FontsConfig}
	 */
	public static FontsConfigBuilder builder(){
		return new FontsConfigBuilder();
	}
	
	/** {@inheritDoc} */
	@Override
	public List<String> getKeyOrder(){
		return ListUtil.createList(FONTS_FOLDER);
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDefaultFields(){
		
		// Fonts Folder
		addField(StringFormField.builder()
				.key("Fonts Folder").defaultValue(getFontsFolder())
				.rowPos(0).colPos(0)
				.build());
		
		// Font Updates button
		addField(ButtonFormField.builder()
				.key("Update Fonts")
				.actionListener(this::updateFonts)
				.rowPos(1).colPos(0)
				.build());
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
	 * @return The folder to look for/store fonts
	 */
	public String getFontsFolder(){
		return (String) getItem(FONTS_FOLDER);
	}
	
	/**
	 * Sets the folder to look for/store fonts
	 *
	 * @param fontsFolder The folder to look for/store fonts
	 */
	public void setFontsFolder(String fontsFolder){
		setItem(FONTS_FOLDER, fontsFolder);
	}
	
	/**
	 * Grabs the latest fonts from the Tadukoo Fonts project on GitHub and loads them
	 *
	 * @param event The {@link ActionEvent} (not used, but this happens on a button)
	 */
	private void updateFonts(ActionEvent event){
		// Grab items we need
		String fontsFolder = (String) getItem(FONTS_FOLDER);
		EasyLogger logger = (EasyLogger) getItem(LOGGER);
		
		// TODO: Let user know the result, passed or failed
		// TODO: Store current fonts release version
		try{
			// TODO: Grab this via GitHub means - TODO still: simplify it from Tadukoo GitHub?
			// Find the latest release for Tadukoo Fonts to get the fonts zip url to download
			LatestReleaseEndpoint endpoint = new LatestReleaseEndpoint("Tadukooverse", "TadukooFonts");
			GitHubRelease release = endpoint.runEndpoint();
			String fontZipURL = null;
			for(GitHubReleaseAsset asset: release.getAssets()){
				if(StringUtil.equalsIgnoreCase(asset.getName(), "TadukooFonts.zip")){
					fontZipURL = asset.getBrowserDownloadUrl();
				}
			}
			
			// If we couldn't find the fonts zip, give up and log an error
			if(StringUtil.isBlank(fontZipURL)){
				logger.logError("Failed to update fonts - couldn't find fonts zip on GitHub");
				return;
			}
			
			new FileDownloader(this, logger, ListUtil.createList(
					Pair.of(fontZipURL, fontsFolder + "fonts.zip")));
			FileUtil.unzipFile(fontsFolder + "fonts.zip", fontsFolder);
			FontResourceLoader resourceLoader = new FontResourceLoader(true, logger,
					GraphicsEnvironment.getLocalGraphicsEnvironment(), fontsFolder);
			// TODO: Store fonts to load in config / retrieve from fonts.zip?
			List<String> fonts = resourceLoader.loadFonts(ListUtil.createList(FontFamilies.ARIMO.getFamily(), FontFamilies.BANGERS.getFamily(),
					FontFamilies.CALADEA.getFamily(), FontFamilies.CALLIGRASERIF.getFamily(), FontFamilies.CARLITO.getFamily(),
					FontFamilies.COMIC_RELIEF.getFamily(), FontFamilies.COUSINE.getFamily(), FontFamilies.GELASIO.getFamily(),
					FontFamilies.LECKERLI_ONE.getFamily(), FontFamilies.LOBSTER.getFamily(), FontFamilies.ROBOTO.getFamily(),
					FontFamilies.ROBOTO_CONDENSED.getFamily(), FontFamilies.SATISFY.getFamily(), FontFamilies.SELAWIK.getFamily(),
					FontFamilies.SOURCE_CODE_PRO.getFamily(), FontFamilies.TINOS.getFamily(), FontFamilies.WINE_TAHOMA.getFamily()),
					true);
			logger.logInfo("Loaded " + fonts.size() + " fonts");
			for(String font: fonts){
				logger.logInfo("Font Loaded: " + font);
			}
		}catch(Exception e){
			logger.logError("Failed to update fonts", e);
		}
	}
}

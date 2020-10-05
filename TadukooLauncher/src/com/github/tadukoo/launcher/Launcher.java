package com.github.tadukoo.launcher;

import com.github.tadukoo.util.FileUtil;
import com.github.tadukoo.util.ListUtil;
import com.github.tadukoo.util.LoggerUtil;
import com.github.tadukoo.util.logger.EasyLogger;
import com.github.tadukoo.util.lookandfeel.TadukooLookAndFeel;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher{
	public static final String FONTS_FOLDER = "fonts/";
	private static final String LOGS_FOLDER = "logs/";
	public static final String LIBS_FOLDER = "libs/";
	public static final String PROGRAMS_FOLDER = "programs/";
	private static final List<String> folders = ListUtil.createList(FONTS_FOLDER, LOGS_FOLDER,
			LIBS_FOLDER, PROGRAMS_FOLDER);
	public static EasyLogger logger;
	
	/**
	 * Starts the Tadukoo Launcher
	 *
	 * @param args Not used
	 * @throws IOException If something goes wrong in creating the file logger
	 * @throws UnsupportedLookAndFeelException Never ({@link TadukooLookAndFeel#isSupportedLookAndFeel()} always
	 * returns true)
	 */
	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException{
		// Set the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS] [%4$s] [%2$s] %5$s%6$s%n");
		
		// Create Logger
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		logger = new EasyLogger(
				LoggerUtil.createFileLogger(LOGS_FOLDER + "launcher-" + dateString + ".log", Level.INFO));
		
		// Create Folders (in case they don't exist yet)
		for(String folder: folders){
			FileUtil.createDirectory(folder);
		}
		
		// Set the Look & Feel
		UIManager.setLookAndFeel(new TadukooLookAndFeel());
		
		// Start the Main Frame
		SwingUtilities.invokeLater(() -> new LauncherMainFrame().setVisible(true));
	}
}

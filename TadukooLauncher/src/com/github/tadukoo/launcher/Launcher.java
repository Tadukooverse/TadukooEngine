package com.github.tadukoo.launcher;

import com.github.tadukoo.util.lookandfeel.TadukooLookAndFeel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Launcher{
	public static Logger logger;
	
	public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException{
		createFolders();
		createLogger();
		UIManager.setLookAndFeel(new TadukooLookAndFeel());
		SwingUtilities.invokeLater(() -> new LauncherMainFrame().setVisible(true));
	}
	
	private static void createFolders(){
		File programsDir = new File("programs");
		if(!programsDir.mkdir() && !programsDir.exists()){
			throw new IllegalStateException("Failed to create programs folder!");
		}
		
		File libDir = new File("libs");
		if(!libDir.mkdir() && !libDir.exists()){
			throw new IllegalStateException("Failed to create libs folder!");
		}
	}
	
	private static void createLogger() throws IOException{
		logger = Logger.getLogger("LauncherLog");
		String logDirPath = "logs";
		File logDir = new File(logDirPath);
		if(!logDir.mkdir() && !logDir.exists()){
			throw new IllegalStateException("Failed to create logs folder!");
		}
		String logFilePath = "logs/launcher.log";
		File logFile = new File(logFilePath);
		logFile.createNewFile();
		FileHandler fh = new FileHandler(logFilePath);
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}
}

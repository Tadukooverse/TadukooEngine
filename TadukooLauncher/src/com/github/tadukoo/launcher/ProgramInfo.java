package com.github.tadukoo.launcher;

import com.github.tadukoo.engine.FileDownloader;
import com.github.tadukoo.engine.Program;
import com.github.tadukoo.util.map.MapUtil;
import com.github.tadukoo.util.tuple.Pair;
import com.github.tadukoo.util.view.form.AbstractForm;
import com.github.tadukoo.util.view.form.field.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;

public class ProgramInfo extends AbstractForm{
	private static final String TITLE = "Title";
	private static final String DESCRIPTION = "Description";
	
	private String genealogyAPIURL = "https://github.com/Tadukoo/TadukooGenealogy/releases/download/0.0.0.1-Pre-Alpha/GenealogyAPI-0.1-Alpha-SNAPSHOT.jar";
	private String genealogyProgramURL = "https://github.com/Tadukoo/TadukooGenealogy/releases/download/0.0.0.1-Pre-Alpha/GenealogyProgram-0.1-Alpha-SNAPSHOT.jar";
	
	public ProgramInfo(String title, String description){
		super(MapUtil.createMap(Pair.of(TITLE, title), Pair.of(DESCRIPTION, description)));
	}
	
	@Override
	public void setDefaultFields(){
		// Grab the title + description from the map
		String title = (String) getItem(TITLE);
		String description = (String) getItem(DESCRIPTION);
		
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
				.actionListener(e -> launchButton(title))
				.build());
	}
	
	private void launchButton(String option){
		//String option = (String) comboBox.getSelectedItem();
		if("Tadukoo Genealogy".equalsIgnoreCase(option)){
			new FileDownloader(this, Arrays.asList(
					Pair.of(genealogyAPIURL, Launcher.LIBS_FOLDER + "GenealogyAPI.jar"),
					Pair.of(genealogyProgramURL, Launcher.PROGRAMS_FOLDER + "TadukooGenealogy.jar")));
			runProgram("TadukooGenealogy.jar", Collections.singletonList("GenealogyAPI.jar"));
		}else if("Tadukoo Look & Feel Test".equalsIgnoreCase(option)){
			// TODO: File Downloader
			runProgram("TadukooLookAndFeelTest.jar", Collections.emptyList());
		}else if("Stratego".equalsIgnoreCase(option)){
			// TODO: File Downloader
			runProgram("Stratego.jar", Collections.emptyList());
		}else if("Tadukoo Pojo Maker".equalsIgnoreCase(option)){
			runProgram("TadukooPojoMaker.jar", Collections.emptyList());
		}
	}
	
	private void runProgram(String programFileName, List<String> dependencyFileNames){
		List<File> files = new ArrayList<>();
		files.add(new File(Launcher.PROGRAMS_FOLDER + programFileName));
		dependencyFileNames.forEach(fileName -> files.add(new File(Launcher.LIBS_FOLDER + fileName)));
		
		List<URL> urls = new ArrayList<>();
		List<String> classes = new ArrayList<>();
		for(File file: files){
			try{
				urls.add(new URL("jar:file:" + file.getPath() + "!/"));
				JarFile jarFile = new JarFile(file);
				jarFile.stream().forEach(jarEntry -> {
					String name = jarEntry.getName();
					if(name.endsWith(".class")){
						classes.add(name);
					}
				});
			}catch(IOException e){
				Launcher.logger.logError("Failed to load file: " + Launcher.LIBS_FOLDER + file.getName(), e);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				JOptionPane.showMessageDialog(this, sw.toString());
				return;
			}
		}
		
		URLClassLoader jarLoader = new URLClassLoader(urls.toArray(new URL[0]));
		Class<?> programClass = null;
		try{
			for(String className: classes){
				Class<?> clazz = jarLoader.loadClass(className.replaceAll("/", ".").replace(".class", ""));
				Class<?>[] interfaces = clazz.getInterfaces();
				for(Class<?> anInterface : interfaces){
					if(anInterface == Program.class){
						programClass = clazz;
						break;
					}
				}
			}
		}catch(ClassNotFoundException e){
			Launcher.logger.logError("Failed to load classes", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JOptionPane.showMessageDialog(this, sw.toString());
			return;
		}
		
		if(programClass == null){
			Launcher.logger.logError("Did not find a program class");
			return;
		}
		
		try{
			Program plugin = (Program) programClass.getDeclaredConstructor().newInstance();
			if(plugin.load(Launcher.logger)){
				Launcher.logger.logInfo("Loaded program " + plugin.getClass().getCanonicalName() + " successfully");
				plugin.run();
			}
		}catch(InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e){
			Launcher.logger.logError("Failed to load " + programClass.getCanonicalName() + " program", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JOptionPane.showMessageDialog(this, sw.toString());
		}
	}
}

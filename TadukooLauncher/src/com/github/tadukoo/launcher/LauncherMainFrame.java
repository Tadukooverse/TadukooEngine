package com.github.tadukoo.launcher;

import com.github.tadukoo.engine.FileDownloader;
import com.github.tadukoo.engine.Program;
import com.github.tadukoo.engine.ProgressRBCWrapperDelegate;
import com.github.tadukoo.engine.ProgressReadableByteChannelWrapper;
import com.github.tadukoo.util.tuple.Pair;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class LauncherMainFrame extends JFrame{
	private static final String programFolder = "programs/";
	private static final String jarFolder = "libs/";
	
	private String genealogyAPIURL = "https://github.com/Tadukoo/TadukooGenealogy/releases/download/0.0.0.1-Pre-Alpha/GenealogyAPI-0.1-Alpha-SNAPSHOT.jar";
	private String genealogyProgramURL = "https://github.com/Tadukoo/TadukooGenealogy/releases/download/0.0.0.1-Pre-Alpha/GenealogyProgram-0.1-Alpha-SNAPSHOT.jar";
	
	private JPanel panel;
	private JTabbedPane tabbedPane;
	private JLabel label;
	private JComboBox<String> comboBox;
	private JButton button;
	private JButton updateButton;
	
	public LauncherMainFrame(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		URL iconURL = this.getClass().getResource("/img/Logo w-o Tagline.png");
		setIconImage(Toolkit.getDefaultToolkit().getImage(iconURL));
		
		panel = new JPanel();
		add(panel);
		
		/*
		label = new JLabel("Choose an app to run");
		panel.add(label);
		
		comboBox = new JComboBox<>(new String[]{"TadukooGenealogy.jar", "TadukooLookAndFeelTest.jar"});
		panel.add(comboBox);
		
		button = new JButton("Launch");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				launchButton();
			}
		});
		panel.add(button);
		
		updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// "Update"
				// TODO: If the engine/launcher itself needs updated, update includes an update application that
				// will close the actual engine/launcher, update the files, and re-open it (avoiding this garbage)
				try{
					Runtime.getRuntime().exec("TadukooLauncher.exe");
					System.exit(0);
				}catch(IOException ex){
					Launcher.logger.log(Level.SEVERE, "Failed to restart launcher", ex);
				}
			}
		});
		panel.add(updateButton);
		/**/
		
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		
		tabbedPane = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
		
		layout.putConstraint(SpringLayout.NORTH, panel,
							5,
								SpringLayout.NORTH, tabbedPane);
		layout.putConstraint(SpringLayout.SOUTH, panel,
								5,
									SpringLayout.SOUTH, tabbedPane);
		layout.putConstraint(SpringLayout.WEST, panel,
								5,
									SpringLayout.WEST, tabbedPane);
		layout.putConstraint(SpringLayout.EAST, panel,
								5,
									SpringLayout.EAST, tabbedPane);
		SpringLayout.Constraints cons = layout.getConstraints(panel);
		cons.setWidth(Spring.constant(800));
		cons.setHeight(Spring.constant(600));
		SpringLayout.Constraints tabCons = layout.getConstraints(tabbedPane);
		tabCons.setWidth(cons.getWidth());
		tabCons.setHeight(cons.getHeight());
		
		createTabComponents("Tadukoo Genealogy", "It's about genealogy", 0);
		createTabComponents("Tadukoo Look & Feel Test", "It's a look and feel test", 1);
		
		panel.add(tabbedPane);
		
		pack();
	}
	
	private void createTabComponents(String title, String description, int index){
		// Make the tab content
		JPanel tabContentPanel = new JPanel();
		tabContentPanel.add(new JLabel(title));
		JTextField descriptionField = new JTextField(description);
		descriptionField.setEditable(false);
		tabContentPanel.add(descriptionField);
		JButton launchButton = new JButton("Launch");
		launchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				launchButton(title);
			}
		});
		tabContentPanel.add(launchButton);
		tabbedPane.addTab(title, tabContentPanel);
		
		// Make the tab info
		JPanel tabPanel = new JPanel();
		JLabel titleLabel = new JLabel(title);
		tabPanel.add(titleLabel);
		tabbedPane.setTabComponentAt(index, tabPanel);
	}
	
	private void launchButton(String option){
		//String option = (String) comboBox.getSelectedItem();
		if("Tadukoo Genealogy".equalsIgnoreCase(option)){
			new FileDownloader(this, Arrays.asList(
					Pair.of(genealogyAPIURL, jarFolder + "GenealogyAPI.jar"),
					Pair.of(genealogyProgramURL, programFolder + "TadukooGenealogy.jar")));
			runProgram("TadukooGenealogy.jar", Collections.singletonList("GenealogyAPI.jar"));
		}else if("Tadukoo Look & Feel Test".equalsIgnoreCase(option)){
			// TODO: File Downloader
			runProgram("TadukooLookAndFeelTest.jar", Collections.emptyList());
		}
	}
	
	private void runProgram(String programFileName, List<String> dependencyFileNames){
		List<File> files = new ArrayList<>();
		files.add(new File(programFolder + programFileName));
		dependencyFileNames.forEach(fileName -> files.add(new File(jarFolder + fileName)));
		
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
				Launcher.logger.log(Level.SEVERE, "Failed to load file: " + jarFolder + file.getName(), e);
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
			Launcher.logger.log(Level.SEVERE, "Failed to load classes", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JOptionPane.showMessageDialog(this, sw.toString());
			return;
		}
		
		if(programClass == null){
			Launcher.logger.log(Level.SEVERE, "Did not find a program class");
			return;
		}
		
		try{
			Program plugin = (Program) programClass.getDeclaredConstructor().newInstance();
			if(plugin.load()){
				System.out.println("Loaded program " + plugin.getClass().getCanonicalName() + " successfully");
				plugin.run();
			}
		}catch(InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e){
			Launcher.logger.log(Level.SEVERE, "Failed to load " + programClass.getCanonicalName() + " program", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JOptionPane.showMessageDialog(this, sw.toString());
		}
	}
}

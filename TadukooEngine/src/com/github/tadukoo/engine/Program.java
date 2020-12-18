package com.github.tadukoo.engine;

import com.github.tadukoo.util.logger.EasyLogger;

import javax.swing.*;

public interface Program{
	
	String getTitle();
	
	boolean load(EasyLogger logger);
	
	JPanel getMainForm();
	
	default void run(){
		SwingUtilities.invokeLater(() -> new MainFrame(this).setVisible(true));
	}
}

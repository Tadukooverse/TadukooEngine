package com.github.tadukoo.engine;

import javax.swing.*;

public class MainFrame extends JFrame{
	
	public MainFrame(Program program){
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		add(program.getMainForm());
		
		pack();
	}
}

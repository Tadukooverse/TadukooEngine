package com.github.tadukoo.engine;

import com.github.tadukoo.util.logger.EasyLogger;
import com.github.tadukoo.view.form.main.MainForm;

import javax.swing.*;

/**
 * Program is an interface used for Programs made for the engine. It serves as the main class for these programs,
 * and provides information, loading, and launching for the programs.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public interface Program{
	
	/** @return The title of the Program */
	String getTitle();
	
	/**
	 * Loads the Program. The {@link EasyLogger logger} that's given should be saved for future use by the
	 * Program (assuming further logging is desired past just loading), as the {@link EasyLogger logger} will
	 * not be provided again later in the process.
	 *
	 * @param logger The {@link EasyLogger} to be used for logging by the program (during loading + future use)
	 * @return Whether the Program loaded successfully or not
	 */
	boolean load(EasyLogger logger);
	
	/**
	 * @return The {@link MainForm} to be used in this Program. It will be launched in the {@link #run()} method
	 */
	MainForm getMainForm();
	
	/**
	 * Launches the Program. The default behavior here is to launch the {@link #getMainForm() MainForm}, wrapped
	 * in an invokeLater
	 */
	default void run(){
		SwingUtilities.invokeLater(() -> getMainForm().launch());
	}
}

package com.github.tadukoo.engine;

import com.github.tadukoo.engine.info.ProgramInfo;

/**
 * Program Handler is used to handle actions necessary for Programs, such as launching them or downloading files.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public interface ProgramHandler{
	
	/**
	 * Launches the Program specified by the given {@link ProgramInfo}.
	 *
	 * @param programInfo The {@link ProgramInfo} for the program to be launched
	 */
	void launchProgram(ProgramInfo programInfo);
}

package com.github.tadukoo.engine;

import com.github.tadukoo.util.logger.EasyLogger;

public interface Program{
	
	boolean load(EasyLogger logger);
	
	void run();
}

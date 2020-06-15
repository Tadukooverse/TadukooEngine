package com.github.tadukoo.engine;

public interface ProgressRBCWrapperDelegate{
	
	public void progressUpdate(double progress, long readSoFar, long expectedSize);
}

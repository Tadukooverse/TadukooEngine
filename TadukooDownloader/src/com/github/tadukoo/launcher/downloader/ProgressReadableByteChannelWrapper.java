package com.github.tadukoo.launcher.downloader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ProgressReadableByteChannelWrapper implements ReadableByteChannel{
	private final ReadableByteChannel byteChannel;
	private final long expectedSize;
	private long readSoFar;
	
	public ProgressReadableByteChannelWrapper(ReadableByteChannel byteChannel, long expectedSize){
		this.byteChannel = byteChannel;
		this.expectedSize = expectedSize;
	}
	
	public void close() throws IOException{
		byteChannel.close();
	}
	
	public boolean isOpen(){
		return byteChannel.isOpen();
	}
	
	public int read(ByteBuffer bb) throws IOException{
		int n;
		double progress;
		
		if((n = byteChannel.read(bb)) > 0){
			readSoFar += n;
			progress = expectedSize > 0 ? (double) readSoFar/(double) expectedSize * 100.0:-1.0;
			LauncherDownloader.progressUpdate(progress, readSoFar, expectedSize);
		}
		
		return n;
	}
}

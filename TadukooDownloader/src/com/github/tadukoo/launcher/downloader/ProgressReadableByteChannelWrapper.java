package com.github.tadukoo.launcher.downloader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Progress Readable Byte Channel Wrapper is a wrapper around {@link ReadableByteChannel} that will send
 * progress updates to {@link LauncherDownloader}, which is keeping track of progress.
 *
 * @author Logan Ferree (Tadukoo)
 * @version Alpha v.0.1
 */
public class ProgressReadableByteChannelWrapper implements ReadableByteChannel{
	/** The underlying {@link ReadableByteChannel} to use for reading */
	private final ReadableByteChannel byteChannel;
	/** The expected total size in bytes to be read */
	private final long expectedSize;
	/** The amount of bytes read so far */
	private long readSoFar;
	
	/**
	 * Wraps the given {@link ReadableByteChannel} so we can send progress updates to {@link LauncherDownloader},
	 * which is keeping track of progress
	 *
	 * @param byteChannel The underlying {@link ReadableByteChannel} to be wrapped
	 * @param expectedSize The expected total size in bytes to be read
	 */
	public ProgressReadableByteChannelWrapper(ReadableByteChannel byteChannel, long expectedSize){
		this.byteChannel = byteChannel;
		this.expectedSize = expectedSize;
	}
	
	/**
	 * Closes the underlying {@link #byteChannel}
	 *
	 * @throws IOException If anything goes wrong in closing the byte channel
	 */
	@Override
	public void close() throws IOException{
		byteChannel.close();
	}
	
	/**
	 * @return Whether the underlying {@link #byteChannel} is open or not
	 */
	@Override
	public boolean isOpen(){
		return byteChannel.isOpen();
	}
	
	/**
	 * Reads bytes from the underlying {@link #byteChannel} into the given {@link ByteBuffer} and
	 * will send a progress update to the LauncherDownloader, which is keeping track of progress
	 *
	 * @param bb The {@link ByteBuffer} to be read into
	 * @return The number of bytes read, possibly 0 or -1 if we're at the end of stream
	 * @throws IOException If anything goes wrong in reading bytes
	 */
	@Override
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

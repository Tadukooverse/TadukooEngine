package com.github.tadukoo.engine;

import com.github.tadukoo.util.ExceptionUtil;
import com.github.tadukoo.util.tuple.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;

public class FileDownloader extends JPanel implements ProgressRBCWrapperDelegate{
	private JProgressBar overallProgressBar;
	private JProgressBar singleProgressBar;
	
	public FileDownloader(JFrame frame, Collection<Pair<String, String>> fileAddressAndPathPairs){
		JPanel panel = new JPanel();
		
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					loadFiles(fileAddressAndPathPairs);
				}catch(IOException ex){
					// TODO: Fix logger stuff
					JOptionPane.showMessageDialog(frame, ExceptionUtil.getStackTraceAsString(ex));
				}
			}
		});
		panel.add(button);
		
		overallProgressBar = new JProgressBar(0, fileAddressAndPathPairs.size());
		overallProgressBar.setStringPainted(true);
		panel.add(overallProgressBar);
		
		singleProgressBar = new JProgressBar(0, 100);
		singleProgressBar.setStringPainted(true);
		panel.add(singleProgressBar);
		
		JOptionPane.showOptionDialog(frame, panel, "File Download", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);
	}
	
	@Override
	public void progressUpdate(double progress, long bytesSoFar, long expectedSize){
		if(progress > 100.0){
			progress = 100.0;
			bytesSoFar = expectedSize;
		}
		singleProgressBar.setValue((int) progress);
		singleProgressBar.setString(progress + "% - " + bytesSoFar + "/" + expectedSize + " bytes");
	}
	
	private int getFileSize(URL url){
		HttpURLConnection connection;
		int fileLength = -1;
		
		try{
			HttpURLConnection.setFollowRedirects(false);
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");
			
			fileLength = connection.getContentLength();
		}catch(Exception e){
			// TODO: Fix Logger situation
			//Launcher.logger.log(Level.WARNING, "Failed to get file size at url: " + url.getPath(), e);
		}
		
		return fileLength;
	}
	
	private void loadFiles(Collection<Pair<String, String>> fileAddressAndPathPairs) throws IOException{
		int done = 0;
		int total = fileAddressAndPathPairs.size();
		overallProgressBar.setValue(done);
		overallProgressBar.setString(done + "/" + total + " files");
		for(Pair<String, String> fileAddressAndPath: fileAddressAndPathPairs){
			loadFile(fileAddressAndPath.getLeft(), fileAddressAndPath.getRight());
			done++;
			overallProgressBar.setValue(done);
			overallProgressBar.setString(done + "/" + total + " files");
		}
	}
	
	private void loadFile(String address, String filePath) throws IOException{
		// Check if file already exists so we don't need to download it
		File file = new File(filePath);
		if(file.exists()){
			return;
		}
		
		URL url = new URL(address);
		singleProgressBar.setValue(0);
		ReadableByteChannel fileDownload = new ProgressReadableByteChannelWrapper(
				Channels.newChannel(url.openStream()), this, getFileSize(url));
		HttpURLConnection.setFollowRedirects(true);
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		FileChannel fileChannel = fileOutputStream.getChannel();
		fileChannel.transferFrom(fileDownload, 0, Long.MAX_VALUE);
		fileDownload.close();
		fileChannel.close();
		fileOutputStream.close();
	}
}

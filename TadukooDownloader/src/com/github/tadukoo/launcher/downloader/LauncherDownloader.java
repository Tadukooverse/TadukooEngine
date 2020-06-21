package com.github.tadukoo.launcher.downloader;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class downloads the Tadukoo Launcher installer exe from GitHub (showing download
 * progress as it goes) and then runs that installer and exits this process.
 *
 * @author Logan Ferree (Tadukoo)
 * @version 0.1-Alpha-SNAPSHOT
 */
public class LauncherDownloader{
	/**
	 * This pattern is used to grab the file size and download URL of the TadukooLauncher exe to
	 * install the Tadukoo Launcher. This pattern matches against the JSON returned by GitHub's
	 * "latest release" endpoint. This is kinda a cheaty way to handle this, but I don't want
	 * to actually handle the JSON, as I'm trying to produce a smaller jar
	 */
	private static final Pattern githubPattern = Pattern.compile(
			"(?:[\\s\\S]*)\"assets\":" +
			"\\s*\\[(?:[\\s\\S]*)\\{\\s*" +
			"\"url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"id\":\\s*(?:\\d*),\\s*" +
			"\"node_id\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"name\":\\s*\"TadukooLauncher-(?:[^\"]*).exe\",\\s*" +
			"\"label\":\\s*(?:[^{}]*),\\s*" +
			"\"uploader\":\\s*\\{\\s*" +
			"\"login\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"id\":\\s*(?:\\d*),\\s*" +
			"\"node_id\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"avatar_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"gravatar_id\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"html_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"followers_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"following_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"gists_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"starred_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"subscriptions_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"organizations_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"repos_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"events_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"received_events_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"type\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"site_admin\":\\s*(?:true|false)\\s*},\\s*" +
			"\"content_type\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"state\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"size\":\\s*(\\d*),\\s*" +
			"\"download_count\":\\s*(?:\\d*),\\s*" +
			"\"created_at\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"updated_at\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"browser_download_url\":\\s*\"([^\"]*)\"\\s*}(?:[\\s\\S]*)\\s*],\\s*" +
			"\"tarball_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"zipball_url\":\\s*\"(?:[^\"]*)\",\\s*" +
			"\"body\":\\s*\"(?:[\\s\\S]*)\"\\s*}");
	
	private static final long bytesInKB = 1024;
	private static final long bytesInMB = bytesInKB * 1024;
	private static final long bytesInGB = bytesInMB * 1024;
	
	private static JProgressBar progressBar;
	private static URL fileURL;
	private static long fileSize;
	private static final String fileName = "TadukooLauncherInstall.exe";
	
	public static void main(String[] args) throws IOException{
		setupFrame();
		retrieveInfoFromGitHub();
		downloadFile();
		// Run the installer and exit
		Runtime.getRuntime().exec(fileName);
		System.exit(0);
	}
	
	private static void setupFrame(){
		// Setup the frame
		JFrame frame = new JFrame();
		frame.setTitle("Tadukoo Downloader");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Setup the panel
		JPanel panel = new JPanel();
		frame.add(panel);
		
		// Add the label
		JLabel label = new JLabel("Downloading Tadukoo Launcher Installer...");
		label.setHorizontalAlignment(JLabel.CENTER);
		panel.add(label);
		
		// Add the progress bar
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setString("Waiting for info from GitHub");
		panel.add(progressBar);
		
		// Setup the layout
		SpringLayout layout = new SpringLayout();
		panel.setLayout(layout);
		// Vertical Alignment
		layout.putConstraint(SpringLayout.NORTH, label,
							25,
							SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, progressBar,
							5,
							SpringLayout.SOUTH, label);
		layout.putConstraint(SpringLayout.SOUTH, panel,
							25,
							SpringLayout.SOUTH, progressBar);
		// Horizontal Alignment
		layout.putConstraint(SpringLayout.WEST, label,
							25,
							SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.WEST, progressBar,
							25,
							SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, panel,
							25,
							SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.EAST, progressBar,
							0,
							SpringLayout.EAST, label);
		frame.pack();
		// Sets the frame to the center of the screen - must happen after pack is called
		frame.setLocationRelativeTo(null);
		
		// Make the frame visible
		frame.setVisible(true);
	}
	
	/**
	 * Retrieves the file URL and size from GitHub
	 *
	 * @throws IOException If most anything goes wrong
	 */
	private static void retrieveInfoFromGitHub() throws IOException{
		// Grab latest release off GitHub
		URL url = new URL("https://api.github.com/repos/Tadukoo/TadukooEngine/releases/latest");
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuilder contentBuffer = new StringBuilder();
		while((inputLine = in.readLine()) != null){
			contentBuffer.append(inputLine);
		}
		in.close();
		connection.disconnect();
		
		// Parse the content to get the download information
		String content = contentBuffer.toString();
		Matcher matcher = githubPattern.matcher(content);
		if(matcher.matches()){
			fileSize = Long.parseLong(matcher.group(1));
			fileURL = new URL(matcher.group(2));
		}else{
			// TODO: Write error log
			throw new IllegalStateException("Failed to get info from GitHub");
		}
	}
	
	public static void progressUpdate(double progress, long readSoFar, long expectedSize){
		if(progress > 100.0){
			progress = 100.0;
			readSoFar = expectedSize;
		}
		
		// Unit conversion
		String units = "bytes";
		long realReadSoFar = readSoFar;
		long realExpectedSize = expectedSize;
		if(realExpectedSize >= bytesInGB){
			realReadSoFar = realReadSoFar/bytesInGB;
			realExpectedSize = realExpectedSize/bytesInGB;
			units = "GB";
		}else if(realExpectedSize >= bytesInMB){
			realReadSoFar = realReadSoFar/bytesInMB;
			realExpectedSize = realExpectedSize/bytesInMB;
			units = "MB";
		}else if(realExpectedSize >= bytesInKB){
			realReadSoFar = realReadSoFar/bytesInKB;
			realExpectedSize = realExpectedSize/bytesInKB;
			units = "KB";
		}
		
		progressBar.setValue((int) progress);
		progressBar.setString(progress + "% - " + realReadSoFar + "/" + realExpectedSize + " " + units);
	}
	
	/**
	 * Download the file from GitHub, using the {@link ProgressReadableByteChannelWrapper} so
	 * that we can update the progress bar.
	 *
	 * @throws IOException If basically anything goes wrong
	 */
	private static void downloadFile() throws IOException{
		progressBar.setValue(0);
		ReadableByteChannel fileDownload = new ProgressReadableByteChannelWrapper(
				Channels.newChannel(fileURL.openStream()), fileSize);
		HttpURLConnection.setFollowRedirects(true);
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		FileChannel fileChannel = fileOutputStream.getChannel();
		fileChannel.transferFrom(fileDownload, 0, Long.MAX_VALUE);
		fileDownload.close();
		fileChannel.close();
		fileOutputStream.close();
	}
}

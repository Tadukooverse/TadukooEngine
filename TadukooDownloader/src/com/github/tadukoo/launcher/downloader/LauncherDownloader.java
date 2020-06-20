package com.github.tadukoo.launcher.downloader;

import java.net.MalformedURLException;
import java.net.URL;

public class LauncherDownloader{
	// TODO: Use REST calls to GitHub to find latest release of the installer, download it, and run it
	public static void main(String[] args) throws MalformedURLException{
		URL url = new URL("https://api.github.com/repos/Tadukoo/TadukooEngine/releases");
		
	}
}

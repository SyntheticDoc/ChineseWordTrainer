/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinesewordtrainer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Deus
 */
public class SoundFileHandler {
    private static SoundFileHandler instance;
    private static ConsoleHandler console;
    private ArrayList<SoundFile> audioFiles;

    private SoundFileHandler(ConsoleHandler console) {
	this.console = console;
	listAudioFiles();
    }

    public static SoundFileHandler getInstance(ConsoleHandler console) {
	if (instance == null) {
	    instance = new SoundFileHandler(console);
	}

	return instance;
    }

    public ArrayList<SoundFile> getAudioFiles() {
	return audioFiles;
    }

    private void listAudioFiles() {
	audioFiles = new ArrayList<>();
	String folder = "audio/";

	try {
	    URL url = SoundFileHandler.class.getClassLoader().getResource(folder);
	    if (url == null) {
		throw new IOException("Could not get URL resource to load sound files, url was null");
	    }

	    // Fall 1: läuft im IDE (Dateisystem)
	    if (url.getProtocol().equals("file")) {
		Path dir = Path.of(url.toURI());
		try ( var stream = Files.list(dir)) {
		    stream.filter(p -> p.toString().endsWith(".mp3"))
			    .forEach(p -> audioFiles.add(getSoundFile(p.getFileName().toString())));
		}
	    } // Fall 2: läuft aus einer JAR
	    else if (url.getProtocol().equals("jar")) {
		String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
		try ( JarFile jar = new JarFile(jarPath)) {
		    Enumeration<JarEntry> entries = jar.entries();
		    while (entries.hasMoreElements()) {
			JarEntry e = entries.nextElement();
			if (e.getName().startsWith(folder) && e.getName().endsWith(".mp3")) {
			    audioFiles.add(getSoundFile(e.getName().substring(folder.length())));
			}
		    }
		}
	    }
	} catch (URISyntaxException | IOException e) {
	    e.printStackTrace();
	    console.cerrprintln("Error loading sound files: " + e.getMessage());
	}
    }
    
    private SoundFile getSoundFile(String filename) {
	return new SoundFile(filename, getSoundName(filename), getSoundTone(filename));
    }
    
    private String getSoundName(String file) {
	String temp = file.replace(".mp3", "");
	StringBuilder result = new StringBuilder();
	
	for(int i = 0; i < (temp.length() - 1); i++) {
	    result.append(temp.charAt(i));
	}
	
	return result.toString();
    }
    
    private String getSoundTone(String file) {
	int dotIndex = file.indexOf('.');
	return "" + file.charAt(dotIndex - 1);
    }
}

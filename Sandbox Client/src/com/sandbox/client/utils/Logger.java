package com.sandbox.client.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Summary: This class provides methods to write to a log file instead of the console.
 * Note for later: It might be a good idea to put the logger on a different thread
 * @author Nathan
 *
 */
public class Logger {
	
	public static boolean enabled = true; 	// Set to false to disable logging to a txt file (Could improve performace)
	private static File log;			  	// The file to log to
	private static FileOutputStream stream; // The stream used to write the log
	public enum MessageType { 				// Determines whether the text is logged as info, warning, or error
		INFO,
		WARNING,
		ERROR
	};
	// Gets the character the system uses as new line (\n)
	private static String newline = System.getProperty("line.separator");
	
	// Creates the log file (Called "log.txt") in the program directory and creates a FileOutputStream to write to it
	public static void initializeLogger() {
		log = new File("log.txt");
		
		// The file is created if it doesn't already exist
		if(log.exists() == false) {
			try {
				log.createNewFile();
				stream = new FileOutputStream(log);
			} catch (IOException e) {
				System.out.println("Couldnt' create log file!");
				enabled = false; // Disable the logger
			}
		}
	}
	
	public static void log(String text) {
		log(text, MessageType.INFO);
	}
	
	public static void logWarningMessage(String warning) {
		log(warning, MessageType.WARNING);
	}
	
	public static void logError(String error) {
		log(error, MessageType.ERROR);
	}
	
	public static void log(String text, MessageType severity) {
		// Don't log if the logger isn't enabled
		if(!enabled || log == null)
			return;
		
		// If the stream is null for some reason, try create it
		if(stream == null) {
			try {
				stream = new FileOutputStream(log);
			} catch (FileNotFoundException e) {
				System.out.println("Log stream is null :(");
				return;
			}
		}
		
		// Writes a new message to the log using the format: [{Date}] {Severity}: {text}
		Date d = new Date();
		String line = "[" + d.toString() + "] " + severity + ": " + text + newline;
		try {
			stream.write(line.getBytes());
		} catch (IOException e) {
			System.out.println("Logger failed to write message :(");
		}
	}
	
	// Frees logger resources (Namely the FileOutputStream)
	public static void freeResources() {
		// No need to close a null stream!
		if(stream == null)
			return;
		
		// Closes the log's stream
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

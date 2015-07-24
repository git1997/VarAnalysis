package edu.iastate.symex.util.logging;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author HUNG
 *
 */
public class MyLogger {
	
	/*
	 * Default logger
	 */
	
	private static Logger logger = Logger.getLogger("MyLogger");
	static {
		addOutputType(OutputType.Console);
		setLevel(MyLevel.ALL);
	}
	
	/*
	 * addOutputType
	 */
	
	public static enum OutputType {
		Console,
		File
	}
	
	private static ConsoleHandler consoleHandler;
	private static FileHandler fileHandler;

	public static void addOutputType(OutputType outputType) {
		if (outputType == OutputType.Console) {
			consoleHandler = new ConsoleHandler();
			logger.addHandler(consoleHandler);
		}
		else { // if (outputType == OutputType.File)
			fileHandler = new FileHandler();
			logger.addHandler(fileHandler);
		}
	}
	
	/*
	 * setLevel
	 */
	
	public static void setLevel(Level level) {
		logger.setLevel(level);
	}
	
	/*
	 * log
	 */
	
	public static void log(Level level, String msg) {
		logger.log(level, msg);
	}
	
	public static void log(String msg) {
		logger.log(MyLevel.INFO, msg);
	}
	
	/*
	 * writeLogMessagesToFile
	 */
	
	public static void writeLogMessagesToFile(File file) {
		if (fileHandler != null)
			fileHandler.writeLogMessagesToFile(file);
	}
	
}
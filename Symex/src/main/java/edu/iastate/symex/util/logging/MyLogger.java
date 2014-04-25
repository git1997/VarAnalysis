package edu.iastate.symex.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author HUNG
 *
 */
public class MyLogger {

	private static Logger logger;
	static {
		logger = Logger.getLogger("MyLogger");
		logger.setLevel(Level.ALL);
		logger.addHandler(new MyHandler());
	}
	
	public static void setLevel(Level level) {
		logger.setLevel(level);
	}
	
	public static void log(Level level, String msg) {
		logger.log(level, msg);
	}
	
}

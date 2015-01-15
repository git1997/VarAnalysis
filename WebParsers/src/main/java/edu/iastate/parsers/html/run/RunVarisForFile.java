package edu.iastate.parsers.html.run;

import java.io.File;

import edu.iastate.parsers.html.core.PhpExecuterAndParser;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunVarisForFile {
	
	/**
	 * PHP file to test
	 */	
	public static String PHP_FILE = "/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/index.php";

	/**
	 * The entry point of the program
	 */
	public static void main(String[] args) {
		// Set up timer and logger
		Timer timer = new Timer();
		MyLogger.setLevel(MyLevel.ALL);
		
		/*
		 * Execute the file 
		 */
		MyLogger.log(MyLevel.PROGRESS, "[RunVarisForFile:" + PHP_FILE + "] Started.");
		
		HtmlDocument htmlDocument = new PhpExecuterAndParser().executeAndParse(new File(PHP_FILE));
		
		MyLogger.log(MyLevel.PROGRESS, "[RunVarisForFile:" + PHP_FILE + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		/*
		 * Print results
		 */
		MyLogger.log(MyLevel.INFO, htmlDocument.toIfdefString());
	}
	
}
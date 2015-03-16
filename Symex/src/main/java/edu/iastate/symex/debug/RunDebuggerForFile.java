package edu.iastate.symex.debug;

import java.io.File;

import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunDebuggerForFile {
	
	/**
	 * PHP file to test
	 */	
	public static String PHP_FILE = //"/Work/Data/Web Projects/Server Code/AddressBook-6.2.12/index.php";
									//"/Work/Data/Web Projects/Server Code/SchoolMate-1.5.4/index.php";
									//"/Work/Data/Web Projects/Server Code/UPB-2.2.7-installed/index.php";
									"/Work/Eclipse/workspace/javaEE/Repositories/SymexTesting/quercus-4.0.39/WebContent/WebApps/WordPress-4.1/index.php";
									
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
		MyLogger.log(MyLevel.PROGRESS, "[RunDebuggerForFile:" + PHP_FILE + "] Started.");
		
		DebugInfo debugInfo = new Debugger().debug(new File(PHP_FILE));
		
		MyLogger.log(MyLevel.PROGRESS, "[RunDebuggerForFile:" + PHP_FILE + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		/*
		 * Print results
		 */
		MyLogger.log(MyLevel.INFO, debugInfo.getDataModel().toIfdefString());
		//MyLogger.log(MyLevel.INFO, debugInfo.getTrace().printTraceToString());
	}
	
}
package edu.iastate.symex.run;

import java.io.File;

import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunSymexForFile {
	
	/**
	 * PHP file to test
	 */	
	public static String PHP_FILE = //"/Work/Eclipse/workspace/javaEE/Repositories/SymexTesting/quercus-4.0.39/WebContent/WebApps/TestProject/index.php";
									//"/Work/Eclipse/workspace/javaEE/Repositories/SymexTesting/quercus-4.0.39/WebContent/WebApps/SchoolMate-1.5.4/index.php";
									"/Work/Eclipse/workspace/javaEE/Repositories/SymexTesting/quercus-4.0.39/WebContent/WebApps/WordPress-4.1/index.php";

	/**
	 * The entry point of the program
	 */
	public static void main(String[] args) {
		// Set up timer and logger
		Timer timer = new Timer();
		MyLogger.setLevel(MyLevel.ALL);
		//MyLogger.addOutputType(MyLogger.OutputType.File);
		
		/*
		 * Execute the file 
		 */
		MyLogger.log(MyLevel.PROGRESS, "[RunSymexForFile:" + PHP_FILE + "] Started.");
		
		DataModel dataModel = new PhpExecuter().execute(new File(PHP_FILE));
		
		MyLogger.log(MyLevel.PROGRESS, "[RunSymexForFile:" + PHP_FILE + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		/*
		 * Print results
		 */
		MyLogger.log(MyLevel.INFO, dataModel.toIfdefString());
		//MyLogger.writeLogMessagesToFile(new File("/Users/HUNG/Desktop/logs.txt"));
	}
	
}
package edu.iastate.analysis.references.detection;

import java.io.File;

import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunReferenceDetectorForFile {
	
	/**
	 * PHP file to test
	 */	
	public static String PHP_FILE =	"/Work/Data/Web Projects/Server Code/AddressBook-6.2.12/index.php"; 
									//"/Work/Data/Web Projects/Server Code/SchoolMate-1.5.4/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/TimeClock-1.04/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/UPB-2.2.7/admin_forums.php";
									//"/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/testWebSlice.php";

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
		MyLogger.log(MyLevel.PROGRESS, "[RunReferenceDetectorForFile:" + PHP_FILE + "] Started.");
		
		ReferenceManager referenceManager = new ReferenceDetector().detect(new File(PHP_FILE));
		
		MyLogger.log(MyLevel.PROGRESS, "[RunReferenceDetectorForFile:" + PHP_FILE + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		/*
		 * Print results
		 */
		MyLogger.log(MyLevel.INFO, referenceManager.writeReferenceListToText());
		//new XmlReadWrite().printReferencesToXmlFile(referenceManager.getSortedReferenceList(), new File("/Users/HUNG/Desktop/references.xml");
	}
	
}
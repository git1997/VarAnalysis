package not_used;
//package deprecated;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//import deprecated.entities.EntityManager;
//
//
//import logging.MyLogger;
//import logging.MyLevel;
//
//import edu.iastate.analysis.config.AnalysisConfig;
//import edu.iastate.analysis.references.ReferenceManager;
//import edu.iastate.analysis.references.detection.FindReferencesInFile;
//import util.FileIO;
//import util.Timer;
//
///**
// * 
// * @author HUNG
// *
// */
//public class FindEntitiesInProject {
//	
//	public static String PROJECT_NAME 	= "Base-1.4.5"; //"Base-1.4.5"; //"Mrbs-1.4.1"; //PhpDocumentor-1.4.4"; //"PostfixAdmin-2.3.5"; //"SchoolMate-1.5.4"; //"SquirrelMail-1.4.22";
//	
//	private String projectFolder;
//	private String outputFile;
//	
//	private boolean readReferencesFromXmlFile;	// Set this option to true to read the references from an XML file rather than re-detecting the references.
//
//	/**
//	 * The entry point of the program.
//	 */
//	public static void main(String[] args) {
//		new FindEntitiesInProject(PROJECT_NAME).execute();
//	}
//	
//	/**
//	 * Constructor.
//	 */
//	public FindEntitiesInProject(String projectFolder, String outputFile, boolean readReferencesFromXmlFile) {
//		this.projectFolder = projectFolder;
//		this.outputFile = outputFile;
//		this.readReferencesFromXmlFile = readReferencesFromXmlFile;
//	}
//	
//	/**
//	 * Constructor.
//	 */
//	public FindEntitiesInProject(String projectName) {
//		this(AnalysisConfig.getProjectFolder(projectName), AnalysisConfig.getEntitiesOutputFile(projectName), false);
//	}
//	
//	/**
//	 * Executes the project.
//	 */
//	public EntityManager execute() {
//		MyLogger.setLevel(MyLevel.PROGRESS); // TODO: [Debug] Comment out this line to run in verbose mode. 
//		
//		Timer timer = new Timer();
//		MyLogger.log(MyLevel.PROGRESS, "[FindEntitiesInProject:" + projectFolder.substring(projectFolder.lastIndexOf("\\") + 1) + "] Started.");
//		
//		// Step 1: Collect entities
//		EntityManager entityManager = collectEntitiesFromFiles();
//		
//		// Step 2: Print results
//		printResults(entityManager);
//		
//		MyLogger.log(MyLevel.PROGRESS, "[FindEntitiesInProject:" + projectFolder.substring(projectFolder.lastIndexOf("\\") + 1) + "] Done in " + timer.getElapsedSecondsInText() + ".");
//		
//		return entityManager;
//	}
//	
//	/**
//	 * Collects entities from files.
//	 */
//	private EntityManager collectEntitiesFromFiles() {
//		EntityManager entityManager = new EntityManager();
//
//		ArrayList<String> phpFiles = FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php"}); // TODO: Add .html, .js
//		Collections.sort(phpFiles, new Comparator<String>()  {
//			@Override
//            public int compare(String str1, String str2) {
//                return str1.length() - str2.length();
//            }        
//        }); // Sort PHP files based on length
//		
//		for (String absoluteFilePath : phpFiles) {
//			String projectName = projectFolder.substring(projectFolder.lastIndexOf("\\") + 1);
//			String relativeFilePath = absoluteFilePath.substring(projectFolder.length() + 1);
//			
//			ReferenceManager referenceManager;
//			if (readReferencesFromXmlFile) {
//				FindReferencesInFile findReferencesInFile = new FindReferencesInFile(projectName, relativeFilePath);
//				referenceManager = findReferencesInFile.readReferencesFromXmlFile();
//			}
//			else {
//				FindReferencesInFile findReferencesInFile = new FindReferencesInFile(projectFolder, relativeFilePath, null);				
//				referenceManager = findReferencesInFile.execute();
//			}
//			entityManager.addReferencesInPage(relativeFilePath, referenceManager.getReferenceList());
//		}
//		
//		entityManager.linkPhpRefsToHtmlEntities();
//		
//		return entityManager;
//	}
//	
//	/**
//	 * Prints results.
//	 */
//	private void printResults(EntityManager entityManager) {
//		if (outputFile != null) {
//			FileIO.cleanFile(outputFile);
//			entityManager.printEntitiesToXmlFile(outputFile);
//			//entityManager.printDanglingReferencesToXmlFile(outputFile);
//		}
//	}
//	
//}

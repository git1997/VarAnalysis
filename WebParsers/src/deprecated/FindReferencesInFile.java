package deprecated;

import java.io.File;

import references.ReferenceManager;
import references.detection.ReferenceDetector;

import logging.MyLogger;
import logging.MyLevel;
import main.CreateDataModelForFile;

import config.DataModelConfig;
import datamodel.DataModel;
import util.FileIO;
import util.Timer;

/**
 * 
 * @author HUNG
 *
 */
public class FindReferencesInFile {
	
	public static String PROJECT_NAME 			= "SchoolMate-1.5.4"; //"test-project";
	public static String RELATIVE_FILE_PATH 	= "index.php"; //"test.php";
	
	private String projectFolder;
	private String relativeFilePath;
	private String outputFile;
	
	/**
	 * The entry point of the program.
	 */
	public static void main(String[] args) {
		new FindReferencesInFile(PROJECT_NAME, RELATIVE_FILE_PATH).execute();
	}
	
	/**
	 * Constructor.
	 */
	public FindReferencesInFile(String projectFolder, String relativeFilePath, String outputFile) {
		this.projectFolder = projectFolder;
		this.relativeFilePath = relativeFilePath;
		this.outputFile = outputFile;
	}
	
	/**
	 * Constructor.
	 */
	public FindReferencesInFile(String projectName, String relativeFilePath) {
		this(WebEntitiesConfig.getProjectFolder(projectName), relativeFilePath, WebEntitiesConfig.getReferencesOutputFile(projectName, relativeFilePath));
	}
	
	/**
	 * Executes the file.
	 */
	public ReferenceManager execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + relativeFilePath + "] Started.");
		
		// Step 1: Create the data model
		ReferenceManager referenceManager = new ReferenceManager();
		DataModel dataModel = createDataModel(referenceManager);
		
		// Step 2: Find references in the data model
		//TODO Comment out the statement below to stop detecting embedded entities.
		findReferencesInDataModel(dataModel, referenceManager, projectFolder);
		
		// Step 3: Print results
		printResults(referenceManager);
		
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + relativeFilePath + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		return referenceManager;
	}
	
	/**
	 * Creates the data model.
	 */
	private DataModel createDataModel(final ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + relativeFilePath + "] Creating data model...");
		
		ReferenceDetector.findReferencesInPhpCode(relativeFilePath, referenceManager); // Also find references while building D-model (e.g. $_REQUEST variables)
		
		CreateDataModelForFile createDataModelForFile = new CreateDataModelForFile(projectFolder, relativeFilePath, null, DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT);
		DataModel dataModel = createDataModelForFile.execute();
		
		ReferenceDetector.findReferencesInPhpCodeFinished();
		
		return dataModel;
	}
	
	/**
	 * Finds references in the data model.
	 */
	private void findReferencesInDataModel(DataModel dataModel, ReferenceManager referenceManager, String projectFolder) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + relativeFilePath + "] Finding references from data model...");

		ReferenceDetector.findReferencesInDataModel(dataModel, referenceManager, projectFolder);
	}
	
	/**
	 * Prints results.
	 */
	private void printResults(ReferenceManager referenceManager) {
		if (outputFile != null) {
			FileIO.cleanFile(outputFile);
			referenceManager.printReferencesToXmlFile(outputFile);
		}
	}
	
	/**
	 * Reads the references from an XML file.
	 * Re-detect them and write to the file if the file does not exist.
	 */
	public ReferenceManager readReferencesFromXmlFile() {
		if (outputFile == null || !new File(outputFile).exists()) {
			String projectName = projectFolder.substring(projectFolder.lastIndexOf("\\") + 1);
			FindReferencesInFile findReferencesInFile = new FindReferencesInFile(projectName, relativeFilePath);
			findReferencesInFile.execute();
			return ReferenceManager.readReferencesFromXmlFile(findReferencesInFile.outputFile);
		}
		else {
			return ReferenceManager.readReferencesFromXmlFile(outputFile);
		}
	}
	
}

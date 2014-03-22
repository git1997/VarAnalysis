package main;

import logging.MyLevel;
import logging.MyLogger;
import config.DataModelConfig;
import datamodel.DataModel;
import util.FileIO;
import util.Timer;

/**
 * 
 * @author HUNG
 *
 */
public class CreateDataModelForFile {

	public static String PROJECT_NAME 			= "test-project"; //"SchoolMate-1.5.4"; //SquirrelMail-1.4.22"; //"test-project";
	public static String RELATIVE_FILE_PATH 	= "test.php"; //test.php";
	
	private String projectFolder;
	private String relativeFilePath;
	private String outputFolder;
	private int outputOption;

	/**
	 * The entry point of the program.
	 */
	public static void main(String[] args) {
		new CreateDataModelForFile(PROJECT_NAME, RELATIVE_FILE_PATH).execute();
	}
	
	/**
	 * Constructor.
	 */
	public CreateDataModelForFile(String projectFolder, String relativeFilePath, String outputFolder, int outputOption) {
		this.projectFolder = projectFolder;
		this.relativeFilePath = relativeFilePath;
		this.outputFolder = outputFolder;
		this.outputOption = outputOption;
	}
	
	/**
	 * Constructor.
	 */
	public CreateDataModelForFile(String projectFolder, String relativeFilePath, String outputFolder) {
		this(projectFolder, relativeFilePath, outputFolder, DataModelConfig.PRINT_DATA_MODEL_AS_XML);
	}
	
	/**
	 * Constructor.
	 */
	public CreateDataModelForFile(String projectName, String relativeFilePath) {
		this(DataModelConfig.getProjectFolder(projectName), relativeFilePath, DataModelConfig.getOutputFolder(projectName, relativeFilePath));
	}
	
	/**
	 * Executes the file.
	 */
	public DataModel execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[CreateDataModelForFile:" + relativeFilePath + "] Started.");
		
		// Step 1: Create the data model
		DataModel dataModel = createDataModel();
		
		// Step 2: Print results
		printResults(dataModel);
		
		MyLogger.log(MyLevel.PROGRESS, "[CreateDataModelForFile:" + relativeFilePath + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		return dataModel;
	}
	
	/**
	 * Creates data model.
	 */
	private DataModel createDataModel() {
		return new DataModel(projectFolder, relativeFilePath);
	}
	
	/**
	 * Prints results.
	 */
	private void printResults(DataModel dataModel) {
		if ((outputOption & DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT) == DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT) {
			// Do nothing
		}
		else if ((outputOption & DataModelConfig.PRINT_DATA_MODEL_AS_XML) == DataModelConfig.PRINT_DATA_MODEL_AS_XML) {
			FileIO.cleanFolder(outputFolder);
			dataModel.printOutputToXmlFile(outputFolder + "\\" + DataModelConfig.dataModelXmlFile);
		}
		else if ((outputOption & DataModelConfig.PRINT_DATA_MODEL_AS_GRAPH) == DataModelConfig.PRINT_DATA_MODEL_AS_GRAPH) {
			FileIO.cleanFolder(outputFolder);
			FileIO.writeStringToFile(dataModel.printToGraphvizFormat(), outputFolder + "\\" + DataModelConfig.dataModelDotFile);
			FileIO.writeStringToFile("dot -Tpdf " + DataModelConfig.dataModelDotFile +" -o " + DataModelConfig.dataModelPdfFile + "\r\n" + DataModelConfig.dataModelPdfFile, outputFolder + "\\" + DataModelConfig.dataModelBatFile);
		}
	}
	
}

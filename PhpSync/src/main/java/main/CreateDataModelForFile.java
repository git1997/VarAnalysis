package main;

import java.io.File;

import config.DataModelConfig;
import datamodel.DataModel;
import util.FileIO;
import util.Timer;
import util.logging.MyLevel;
import util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class CreateDataModelForFile {

	private final File file;
	private final File workingDirectory;
//	private String outputFolder;
//	private int outputOption;

//	/**
//	 * The entry point of the program.
//	 */
//	public static void main(String[] args) {
//		new CreateDataModelForFile(PROJECT_NAME, RELATIVE_FILE_PATH).execute();
//	}
	
	/**
	 * Constructor.
	 */
	public CreateDataModelForFile(File file, File workingDirectory/*, int outputOption*/) {
		this.file = file;
		this.workingDirectory = workingDirectory;
//		this.outputFolder = outputFolder;
//		this.outputOption = outputOption;
	}
	
/*	*//**
	 * Constructor.
	 *//*
	public CreateDataModelForFile(String projectFolder, String relativeFilePath, String outputFolder) {
		this(projectFolder, relativeFilePath, outputFolder/*, DataModelConfig.PRINT_DATA_MODEL_AS_XML);
	}
	
	*//**
	 * Constructor.
	 *//*
	public CreateDataModelForFile(String projectName, String relativeFilePath) {
		this(DataModelConfig.getProjectFolder(projectName), relativeFilePath, DataModelConfig.getOutputFolder(projectName, relativeFilePath));
	}
	*/
	/**
	 * Executes the file.
	 */
	public DataModel execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[CreateDataModelForFile:" + file + "] Started.");
		
		// Step 1: Create the data model
		DataModel dataModel = new DataModel(file, workingDirectory);
		
		// Step 2: Print results
//		printResults(dataModel);
		
		MyLogger.log(MyLevel.PROGRESS, "[CreateDataModelForFile:" + file + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		return dataModel;
	}
	
	
//	/**
//	 * Prints results.
//	 */
//	private void printResults(DataModel dataModel) {
//		if ((outputOption & DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT) == DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT) {
//			// Do nothing
//		}
//		else if ((outputOption & DataModelConfig.PRINT_DATA_MODEL_AS_XML) == DataModelConfig.PRINT_DATA_MODEL_AS_XML) {
//			FileIO.cleanFolder(outputFolder);
//			dataModel.printOutputToXmlFile(outputFolder + "\\" + DataModelConfig.dataModelXmlFile);
//		}
//		else if ((outputOption & DataModelConfig.PRINT_DATA_MODEL_AS_GRAPH) == DataModelConfig.PRINT_DATA_MODEL_AS_GRAPH) {
//			FileIO.cleanFolder(outputFolder);
//			FileIO.writeStringToFile(dataModel.printToGraphvizFormat(), outputFolder + "\\" + DataModelConfig.dataModelDotFile);
//			FileIO.writeStringToFile("dot -Tpdf " + DataModelConfig.dataModelDotFile +" -o " + DataModelConfig.dataModelPdfFile + "\r\n" + DataModelConfig.dataModelPdfFile, outputFolder + "\\" + DataModelConfig.dataModelBatFile);
//		}
//	}
	
}

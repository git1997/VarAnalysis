package edu.iastate.symex.run;

import java.io.File;

import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.WriteDataModelToIfDefs;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunSymexForFile {
	
	/*
	 * Default files and folders
	 */	
	public static String WORKSPACE 			= "C:/Users/HUNG/Desktop/Lab/Web Projects/workspace";
	public static String SERVER_CODE		= "Server Code";
	public static String DATA_MODEL			= "Data Model";
	
	public static String PROJECT_FOLDER 	= WORKSPACE + "/" + SERVER_CODE + "/{PROJECT_NAME}"; // @see config.DataModelConfig.getProjectFolderForProjectName(String)
	public static String OUTPUT_FOLDER 		= WORKSPACE + "/" + DATA_MODEL + "/{PROJECT_NAME}/{RELATIVE_FILE_PATH}";	// @see config.DataModelConfig.getOutputFolderForFile(String, String)

	public static String PHP_FILE			= "/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/test.php";

	public static String dataModelXmlFile	= "data_model.xml";
	public static String dataModelDotFile	= "data_model.dot";
	public static String dataModelPdfFile	= "data_model.pdf";
	public static String dataModelBatFile	= "data_model.bat";
	
	/*
	 * Output options
	 */
	public static int PRINT_DATA_MODEL_AS_OBJECT	= 1;
	public static int PRINT_DATA_MODEL_AS_XML 		= 2;
	public static int PRINT_DATA_MODEL_AS_GRAPH		= 4;
	
	public static boolean PRINT_TRACING_INFO 		= false; 	// Only effective when PRINT_DATA_MODEL_AS_XML is turned ON
	public static boolean PRINT_CLIENT_OUTPUT_ONLY 	= true;		// Only effective when PRINT_DATA_MODEL_AS_GRAPH is turned ON
	
	private final File file;
	private final File workingDirectory;
//	private String outputFolder;
//	private int outputOption;

//	/**
//	 * The entry point of the program.
//	 */
	public static void main(String[] args) {
		new RunSymexForFile(new File(PHP_FILE), null).execute();
	}
	
	/**
	 * Constructor.
	 */
	public RunSymexForFile(File file, File workingDirectory/*, int outputOption*/) {
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
		MyLogger.log(MyLevel.PROGRESS, "[RunSymexForFile:" + file + "] Started.");
		
		// Step 1: Create the data model
		DataModel dataModel = new PhpExecuter().execute(file, workingDirectory);
		
		// Step 2: Print results
		MyLogger.log(MyLevel.INFO, WriteDataModelToIfDefs.convert(dataModel));
		//printResults(dataModel);
		
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
	

	
	/*
	 * Utility methods
	 */
	
	public static String getProjectFolder(String projectName) {
		return PROJECT_FOLDER.replace("{PROJECT_NAME}", projectName);
	}
	
	public static String getOutputFolder(String projectName, String relativeFilePath) {
		return OUTPUT_FOLDER.replace("{PROJECT_NAME}", projectName).replace("{RELATIVE_FILE_PATH}", relativeFilePath);
	}
	
}

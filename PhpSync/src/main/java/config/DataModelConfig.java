package config;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelConfig {
	
	/*
	 * Default files and folders
	 */	
	public static String WORKSPACE 			= "C:/Users/HUNG/Desktop/Lab/Web Projects/workspace";
	public static String SERVER_CODE		= "Server Code";
	public static String DATA_MODEL			= "Data Model";
	
	public static String PROJECT_FOLDER 	= WORKSPACE + "/" + SERVER_CODE + "/{PROJECT_NAME}"; // @see config.DataModelConfig.getProjectFolderForProjectName(String)
	public static String OUTPUT_FOLDER 		= WORKSPACE + "/" + DATA_MODEL + "/{PROJECT_NAME}/{RELATIVE_FILE_PATH}";	// @see config.DataModelConfig.getOutputFolderForFile(String, String)
	
	public static String dataModelXmlFile	= "data_model.xml";
	public static String dataModelDotFile	= "data_model.dot";
	public static String dataModelPdfFile	= "data_model.pdf";
	public static String dataModelBatFile	= "data_model.bat";
		
	/*
	 * Data Model parameters
	 */
	public static int DATA_MODEL_MAX_DEPTH = 50;	// The maximum depth of Data Model
	public static boolean COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS = false;				// Set to true to let the FinalOutput include the outputs at exit statements.
	public static boolean UNESCAPE_LITERAL_STRING_VALUE_PRESERVING_LENGTH = true;	// Preserve the length when unescaping a literal string value to facilitate source code tracing
	
	/*
	 * Output options
	 */
	public static int PRINT_DATA_MODEL_AS_OBJECT	= 1;
	public static int PRINT_DATA_MODEL_AS_XML 		= 2;
	public static int PRINT_DATA_MODEL_AS_GRAPH		= 4;
	
	public static boolean PRINT_TRACING_INFO 		= false; 	// Only effective when PRINT_DATA_MODEL_AS_XML is turned ON
	public static boolean PRINT_CLIENT_OUTPUT_ONLY 	= true;		// Only effective when PRINT_DATA_MODEL_AS_GRAPH is turned ON
	
	/*
	 * XML identifiers
	 */
	public static String XML_OUTPUT			= "Output";
	public static String XML_LITERAL		= "Literal";
	public static String XML_CONCAT 		= "Concat";
	public static String XML_SELECT			= "Select";
	public static String XML_SELECT_TRUE	= "True";
	public static String XML_SELECT_FALSE	= "False";
	public static String XML_REPEAT			= "Repeat";
	public static String XML_SYMBOLIC		= "Symbolic";

	public static String XML_STRING_VALUE	= "StringValue";
	public static String XML_FILE_PATH		= "File";
	public static String XML_POSITION		= "Position";
	
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

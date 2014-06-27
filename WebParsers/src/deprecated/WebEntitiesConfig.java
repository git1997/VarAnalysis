package deprecated;

/**
 * 
 * @author HUNG
 *
 */
public class WebEntitiesConfig {

	/*
	 * Default files and folders
	 */	
	public static String WEB_ENTITIES				= "Web Entities";
	
	public static String ENTITIES_OUTPUT_FOLDER		= DataModelConfig.WORKSPACE + "/" + WEB_ENTITIES + "/{PROJECT_NAME}";	// @see config.WebEntitiesConfig.getEntitiesOutputFolderForProject(String)
	public static String REFERENCES_OUTPUT_FOLDER 	= DataModelConfig.WORKSPACE + "/" + WEB_ENTITIES + "/{PROJECT_NAME}/{RELATIVE_FILE_PATH}";	// @see config.WebEntitiesConfig.getReferencesOutputFolderForFile(String, String)
	
	public static String entitiesXmlFile			= "__ENTITIES__/entities.xml";
	public static String referencesXmlFile			= "references.xml";
	
	public static String ENTITIES_OUTPUT_FILE		= ENTITIES_OUTPUT_FOLDER + "/" + entitiesXmlFile;
	public static String REFERENCES_OUTPUT_FILE		= REFERENCES_OUTPUT_FOLDER + "/" + referencesXmlFile;
	
	/*
	 * Execution options
	 */
	public static int CONSTRAINT_MAX_LENGTH 		= 500;	// The maximum length of a Constraint
	public static int CONSTRAINT_MAX_ATOMIC_NUM 	= 20;	// The maximum number of atomic predicates in a Constraint
	public static boolean USE_TIMESTAMP_TO_AVOID_REDETECTION = false;	// Not currently used
	
	// Turn ON these options for BabelRef, turn OFF for ERef
	// @see entities.EntityManager.canLinkEntityReference(Entity, RegularReference)
	// @see references.detection.JavascriptVisitor.visit(SimpleName)
	public static boolean DISCARD_CONSTRAINTS_WHEN_COMPARING_ENTITIES = false;	
	public static boolean DETECT_JS_VARIABLES = false; 	
	
	/*
	 * XML identifiers
	 */
	public static String XML_ROOT				= "Root";	
	public static String XML_ENTITY				= "Entity";
	public static String XML_ENT_NAME			= "Name";
	public static String XML_ENT_TYPE			= "Type";
	
	public static String XML_REFERENCE			= "Reference";
	public static String XML_REF_NAME			= "Name";
	public static String XML_REF_TYPE			= "Type";
	public static String XML_CHARACTER			= "Character";	
	public static String XML_FILE_PATH			= "File";
	public static String XML_POSITION			= "Position";
	
	/*
	 * Utility methods
	 */
	
	public static String getProjectFolder(String projectName) {
		return DataModelConfig.getProjectFolder(projectName);
	}
	
	public static String getEntitiesOutputFile(String projectName) {
		return ENTITIES_OUTPUT_FILE.replace("{PROJECT_NAME}", projectName);
	}
	
	public static String getReferencesOutputFile(String projectName, String relativeFilePath) {
		return REFERENCES_OUTPUT_FILE.replace("{PROJECT_NAME}", projectName).replace("{RELATIVE_FILE_PATH}", relativeFilePath);
	}
	
}

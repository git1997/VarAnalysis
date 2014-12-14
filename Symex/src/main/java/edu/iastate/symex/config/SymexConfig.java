package edu.iastate.symex.config;

/**
 * 
 * @author HUNG
 *
 */
public class SymexConfig {

	/*
	 * Execution options
	 */
	public static int DATA_MODEL_MAX_DEPTH = 50;									// The maximum depth of Data Model
	public static boolean COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS = true;				// Set to true to let the FinalOutput include the outputs at exit statements.
	public static boolean COMBINE_CONSECUTIVE_LITERAL_NODES = false; 				// Set to true to combine consecutive literal nodes in a ConcatNode
	
	/*
	 * Constraint options
	 */
	public static int CONSTRAINT_MAX_LENGTH 		= 500;	// The maximum length of a Constraint
	public static int CONSTRAINT_MAX_ATOMIC_NUM 	= 20;	// The maximum number of atomic predicates in a Constraint
	
	/*
	 * XML identifiers
	 */
	public static String XML_DATAMODEL		= "DataModel";
	public static String XML_ARRAY			= "Array";
	public static String XML_CONCAT 		= "Concat";
	public static String XML_LITERAL		= "Literal";
	public static String XML_OBJECT			= "Object";
	public static String XML_REPEAT			= "Repeat";
	public static String XML_SELECT			= "Select";
	public static String XML_SELECT_TRUE	= "True";
	public static String XML_SELECT_FALSE	= "False";
	public static String XML_SYMBOLIC		= "Symbolic";

	public static String XML_TEXT			= "Text";
	public static String XML_FILE			= "File";
	public static String XML_OFFSET			= "Offset";
	public static String XML_LENGTH			= "Length";
	
}

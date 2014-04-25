package edu.iastate.symex.config;

/**
 * 
 * @author HUNG
 *
 */
public class SymexConfig {

	/*
	 * Execution parameters
	 */
	public static int DATA_MODEL_MAX_DEPTH = 50;	// The maximum depth of Data Model
	public static boolean COLLECT_OUTPUTS_FROM_EXIT_STATEMENTS = false;				// Set to true to let the FinalOutput include the outputs at exit statements.
	public static boolean UNESCAPE_LITERAL_STRING_VALUE_PRESERVING_LENGTH = true;	// Preserve the length when unescaping a literal string value to facilitate source code tracing
	public static boolean COMBINING_CONSECUTIVE_LITERAL_NODES = false; 				// Set to true to combine consecutive literal nodes in a ConcatNode
	
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

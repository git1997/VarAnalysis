package edu.iastate.analysis.config;

/**
 * 
 * @author HUNG
 *
 */
public class AnalysisConfig {

	/*
	 * Execution options
	 */
	public static boolean CHECK_CONSTRAINTS_FOR_CROSS_PAGE_DATA_FLOWS = true;	// Set to true to check the constraints when detecting cross-page data flows

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
	
	public static String XML_FILE				= "File";
	public static String XML_OFFSET				= "Offset";
	public static String XML_LENGTH				= "Length";
	
}
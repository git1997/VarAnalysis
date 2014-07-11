package edu.iastate.analysis.config;

/**
 * 
 * @author HUNG
 *
 */
public class AnalysisConfig {

	// Turn ON these options for BabelRef, turn OFF for ERef
	// @see entities.EntityManager.canLinkEntityReference(Entity, RegularReference)
	// @see references.detection.JavascriptVisitor.visit(SimpleName)
	public static boolean DISCARD_CONSTRAINTS_WHEN_COMPARING_ENTITIES = false;	
	public static boolean DETECT_JS_VARIABLES = true; 	
	
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
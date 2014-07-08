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
	public static String XML_POSITION			= "Character";	
	public static String XML_FILE				= "File";
	public static String XML_OFFSET				= "Offset";
	public static String XML_LENGTH				= "Length";
	
}
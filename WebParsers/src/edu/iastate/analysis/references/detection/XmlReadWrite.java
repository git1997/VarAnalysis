package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iastate.analysis.config.AnalysisConfig;
import edu.iastate.analysis.references.HtmlFormDecl;
import edu.iastate.analysis.references.HtmlIdDecl;
import edu.iastate.analysis.references.HtmlInputDecl;
import edu.iastate.analysis.references.HtmlQueryDecl;
import edu.iastate.analysis.references.JsFunctionCall;
import edu.iastate.analysis.references.JsFunctionDecl;
import edu.iastate.analysis.references.JsRefToHtmlForm;
import edu.iastate.analysis.references.JsRefToHtmlId;
import edu.iastate.analysis.references.JsRefToHtmlInput;
import edu.iastate.analysis.references.PhpRefToHtmlEntity;
import edu.iastate.analysis.references.PhpRefToSqlTableColumn;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.SqlTableColumnDecl;
import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.position.RelativeRange;
import edu.iastate.symex.util.XmlDocument;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class XmlReadWrite {
	
	/**
	 * Prints the references to XML file.
	 */
	public void printReferencesToXmlFile(ArrayList<Reference> references, File xmlFile) {		
		Document document = XmlDocument.newDocument();
		Element root = document.createElement(AnalysisConfig.XML_ROOT);
		document.appendChild(root);
		for (Reference reference : references) {
			root.appendChild(printReferenceToXml(reference, document));
		}
		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
	}
	
	/**
	 * Reads the references from XML file.
	 */
	public ArrayList<Reference> readReferencesFromXmlFile(File xmlFile) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		
		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
		Element rootElement = (Element) document.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element referenceElement = (Element) nodeList.item(i);
			Reference reference = readReferenceFromXml(referenceElement);
			references.add(reference);
		}
		
		return references;
	}
	
	/**
	 * Prints the reference to XML.
	 */
	public Element printReferenceToXml(Reference reference, Document document) {
		Element referenceElement = document.createElement(AnalysisConfig.XML_REFERENCE);
		
		referenceElement.setAttribute(AnalysisConfig.XML_REF_TYPE, reference.getType());
		referenceElement.setAttribute(AnalysisConfig.XML_REF_NAME, reference.getName());
		referenceElement.appendChild(printLocationToXml(reference.getLocation(), document));
		
		if (reference instanceof HtmlInputDecl) {
			if (((HtmlInputDecl) reference).getFormName() != null)
				referenceElement.setAttribute("FormName", ((HtmlInputDecl) reference).getFormName());
			if (((HtmlInputDecl) reference).getSubmitToPage() != null)
				referenceElement.setAttribute("SubmitToPage", ((HtmlInputDecl) reference).getSubmitToPage() );
		}
		else if (reference instanceof JsRefToHtmlInput) {
			if (((JsRefToHtmlInput) reference).getFormName() != null)
				referenceElement.setAttribute("FormName", ((JsRefToHtmlInput) reference).getFormName());
		}
		else if (reference instanceof PhpRefToHtmlEntity) {
//			if (((PhpRefToHtmlEntity) reference).getOnPage() != null)
//				referenceElement.setAttribute("OnPage", ((JsRefToHtmlInput) reference).getFormName());
		}
		else if (reference instanceof PhpRefToSqlTableColumn) {
			if (((PhpRefToSqlTableColumn) reference).getScope() != null)
				referenceElement.setAttribute("Scope", ((PhpRefToSqlTableColumn) reference).getScope() );
		}
		else if (reference instanceof SqlTableColumnDecl) {
			if (((SqlTableColumnDecl) reference).getScope() != null)
				referenceElement.setAttribute("Scope", ((SqlTableColumnDecl) reference).getScope() );
		}
		
		return referenceElement;
	}
	
	/**
	 * Reads the reference from XML.
	 */
	public Reference readReferenceFromXml(Element referenceElement) {
		String type = referenceElement.getAttribute(AnalysisConfig.XML_REF_TYPE);
		String name = referenceElement.getAttribute(AnalysisConfig.XML_REF_NAME);
		PositionRange location = readLocationFromXml((Element) referenceElement.getChildNodes().item(0));
		
		Reference reference;
		
		if (type.equals("HtmlFormDecl"))
			reference = new HtmlFormDecl(name, location);
		
		else if (type.equals("HtmlIdDecl"))
			reference = new HtmlIdDecl(name, location);
		
		else if (type.equals("HtmlInputDecl")) {
			String formName = referenceElement.getAttribute("FormName");
			String submitToPage = referenceElement.getAttribute("SubmitToPage");
			reference = new HtmlInputDecl(name, location, formName, submitToPage);
		}
		
		else if (type.equals("HtmlQueryDecl"))
			reference = new HtmlQueryDecl(name, location);
		
		else if (type.equals("JsFunctionCall"))
			reference = new JsFunctionCall(name, location);
		
		else if (type.equals("JsFunctionDecl"))
			reference = new JsFunctionDecl(name, location);
		
		else if (type.equals("JsRefToHtmlForm"))
			reference = new JsRefToHtmlForm(name, location);
		
		else if (type.equals("JsRefToHtmlId"))
			reference = new JsRefToHtmlId(name, location);
		
		else if (type.equals("JsRefToHtmlInput")) {
			String formName = referenceElement.getAttribute("FormName");
			reference = new JsRefToHtmlInput(name, location, formName);
		}
		
		else if (type.equals("PhpRefToHtmlEntity")) {
			String onPage = referenceElement.getAttribute("OnPage");
			reference = new PhpRefToHtmlEntity(name, location, onPage);
		}
		
		else if (type.equals("PhpRefToSqlTableColumn")) {
			String scope = referenceElement.getAttribute("Scope");
			reference = new PhpRefToSqlTableColumn(name, location, scope);
		}
		
		else if (type.equals("SqlTableColumnDecl")) {
			String scope = referenceElement.getAttribute("Scope");
			reference = new SqlTableColumnDecl(name, location, scope);
		}
		
		else {
			reference = null;
			MyLogger.log(MyLevel.USER_EXCEPTION, "In XmlReadWrite.java: Undefined type of reference: " + type);
		}
		
		return reference;
	}
	
	/**
	 * Prints the location to XML
	 */
	public Element printLocationToXml(PositionRange location, Document document) {
		Element positionElement = document.createElement(location.getClass().getSimpleName());
		
		if (location instanceof Range) {
			positionElement.setAttribute(AnalysisConfig.XML_FILE, ((Range) location).getFilePath());
			positionElement.setAttribute(AnalysisConfig.XML_OFFSET, String.valueOf(((Range) location).getOffset()));
			positionElement.setAttribute(AnalysisConfig.XML_LENGTH, String.valueOf(((Range) location).getLength()));
		}
		else if (location instanceof CompositeRange) {
			positionElement.appendChild(printLocationToXml(((CompositeRange) location).getRange1(), document));
			positionElement.appendChild(printLocationToXml(((CompositeRange) location).getRange2(), document));
		}
		else if (location instanceof RelativeRange) {
			positionElement.appendChild(printLocationToXml(((RelativeRange) location).getBasePositionRange(), document));
			positionElement.setAttribute(AnalysisConfig.XML_OFFSET, String.valueOf(((RelativeRange) location).getOffset()));
			positionElement.setAttribute(AnalysisConfig.XML_LENGTH, String.valueOf(((RelativeRange) location).getLength()));
		}
		else { // UndefinedRange
			// Do nothing
		}
		
		return positionElement;
	}
	
	/**
	 * Reads the location from XML
	 */
	public PositionRange readLocationFromXml(Element locationElement) {
		String tagName = locationElement.getTagName();
		PositionRange location;
		
		if (tagName.equals("Range")) {
//			positionElement.setAttribute(AnalysisConfig.XML_FILE, ((Range) location).getFilePath());
//			positionElement.setAttribute(AnalysisConfig.XML_OFFSET, String.valueOf(((Range) location).getOffset()));
//			positionElement.setAttribute(AnalysisConfig.XML_LENGTH, String.valueOf(((Range) location).getLength()));
			location = null;
		}
		else if (tagName.equals("CompositeRange")) {
//			positionElement.appendChild(printLocationToXml(((CompositeRange) location).getRange1(), document));
//			positionElement.appendChild(printLocationToXml(((CompositeRange) location).getRange2(), document));
			location = null;
		}
		else if (tagName.equals("RelativeRange")) {
//			positionElement.appendChild(printLocationToXml(((RelativeRange) location).getBasePositionRange(), document));
//			positionElement.setAttribute(AnalysisConfig.XML_OFFSET, String.valueOf(((RelativeRange) location).getOffset()));
//			positionElement.setAttribute(AnalysisConfig.XML_LENGTH, String.valueOf(((RelativeRange) location).getLength()));
			location = null;
		}
		else { // UndefinedRange
			location = PositionRange.UNDEFINED;
		}
		
		return location;
	}
	
	
	
	
	
	
	
	/*
	 * ENTITY
	 */
	
//	/**
//	 * Prints the entity to an XML element
//	 */
//	public Element printToXmlElement(Document document) {
//		Collections.sort(references, new Reference.ReferenceComparator()); // Sort the references first
//		
//		Element entityElement = document.createElement(AnalysisConfig.XML_ENTITY);
//		entityElement.setAttribute(AnalysisConfig.XML_ENT_NAME, this.getName());
//		entityElement.setAttribute(AnalysisConfig.XML_ENT_TYPE, this.getType());
//		
//		entityElement.setAttribute(AnalysisConfig.XML_FILE, this.getFilePath());
//		for (Reference reference : references) {
//			entityElement.appendChild(reference.printToXmlElement(document));
//		}
//		
//		return entityElement;
//	}
//	
//	/**
//	 * Reads the entity from an XML element
//	 */
//	public static Entity readEntityFromXmlElement(Element entityElement) {
//		return null;
////		Type entityType = Type.valueOf(entityElement.getAttribute(WebEntitiesConfig.XML_REF_TYPE));
////		String entityName = entityElement.getAttribute(WebEntitiesConfig.XML_REF_NAME);
////		Entity entity = new Entity(entityType, entityName, entityName); // Now it's Ok to discard the qualifiedName
////		
////		if (entityElement.hasAttribute(WebEntitiesConfig.XML_ENTITY_INFO1))
////			entity.submitToPage = entityElement.getAttribute(WebEntitiesConfig.XML_ENTITY_INFO1);
////		if (entityElement.hasAttribute(WebEntitiesConfig.XML_REF_INFO2))
////			entity.onPage = entityElement.getAttribute(WebEntitiesConfig.XML_REF_INFO2);
////		
////		NodeList nodeList = entityElement.getChildNodes();
////		for (int i = 0; i < nodeList.getLength(); i++) {
////			Element referenceElement = (Element) nodeList.item(i);
////			Reference.readReferenceFromXmlElement(referenceElement, entity);
////			EntityManager.linkEntityReference(entity, reference);
////		}
////		return entity;
//	}
//	
//	
//	/*
//	 * ENTITY MANAGER
//	 */
//	
//
//	
//	/*
//	 * Print/read entities to/from XML file
//	 */
//
//	/**
//	 * Prints entities to an XML file.
//	 */
//	public void printEntitiesToXmlFile(String xmlFile) {
//		printEntitiesToXmlFile(getEntityListIncludingDanglingRefs(), xmlFile);
//	}
//	
//	/**
//	 * Prints entities to an XML file.
//	 */
//	public static void printEntitiesToXmlFile(ArrayList<Entity> entityList, String xmlFile) {
//		Collections.sort(entityList, new Entity.EntityComparator()); // Sort the entities first
//		
//		Document document = XmlDocument.newDocument();
//		Element root = document.createElement(AnalysisConfig.XML_ROOT);
//		document.appendChild(root);
//		for (Entity entity : entityList) {
//			root.appendChild(entity.printToXmlElement(document));
//		}
//		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
//	}
//	
//	/**
//	 * Reads the entities from an XML file.
//	 */
//	public static ArrayList<Entity> readEntitiesFromXmlFile(String xmlFile) {
//		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
//		Element root = (Element) document.getDocumentElement();
//		NodeList entityElementList = root.getChildNodes();
//		
//		ArrayList<Entity> entityList = new ArrayList<Entity>();
//		for (int i = 0; i < entityElementList.getLength(); i++) {
//			Element entityElement = (Element) entityElementList.item(i);
//			entityList.add(Entity.readEntityFromXmlElement(entityElement));
//		}
//		return entityList;
//	}
//	
//	/**
//	 * Prints dangling references to an XML file.
//	 */
//	public void printDanglingReferencesToXmlFile(String xmlFile) {
//		ReferenceManager referenceManager = new ReferenceManager();
//		for (Reference reference : getDanglingReferenceList())
//			referenceManager.addReference(reference);
//		referenceManager.printReferencesToXmlFile(xmlFile);
//	}
	
}

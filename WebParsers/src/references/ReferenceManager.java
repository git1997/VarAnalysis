package references;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import constraints.OrConstraint;
import deprecated.WebEntitiesConfig;
import util.XmlDocument;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceManager {
	
	/**
	 * Maps a location to a reference
	 */
	private HashMap<String, Reference> mapLocationToReference = new HashMap<String, Reference>();
	
	/*
	 * Update ReferenceManager
	 */
	
	/**
	 * Adds a reference to ReferenceManager.
	 * @param reference
	 */
	public void addReference(Reference reference) {
		String location = reference.getLocationString();
		Reference existingReference = mapLocationToReference.get(location);
		
		if (existingReference != null) {
			if (existingReference.getConstraint() != reference.getConstraint())
				existingReference.setConstraint(new OrConstraint(existingReference.getConstraint(), reference.getConstraint()));
		}
		else {
			mapLocationToReference.put(location, reference);
		}
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<Reference> getReferenceList() {
		return new ArrayList<Reference>(mapLocationToReference.values());
	}
	
	/**
	 * Prints references to an XML file.
	 */
	public void printReferencesToXmlFile(String xmlFile) {
		ArrayList<Reference> referenceList = getReferenceList();
		Collections.sort(referenceList, new Reference.ReferenceComparator()); // // Sort the references first
		
		Document document = XmlDocument.newDocument();
		Element root = document.createElement(WebEntitiesConfig.XML_ROOT);
		document.appendChild(root);
		for (Reference reference : referenceList) {
			root.appendChild(reference.printToXmlElement(document));
		}
		XmlDocument.writeXmlDocumentToFile(document, xmlFile);
	}
	
	/**
	 * Reads references from an XML file.
	 */
	public static ReferenceManager readReferencesFromXmlFile(String xmlFile) {
		ReferenceManager referenceManager = new ReferenceManager();
		
		Document document = XmlDocument.readXmlDocumentFromFile(xmlFile);
		Element rootElement = (Element) document.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element referenceElement = (Element) nodeList.item(i);
			Reference reference = Reference.readReferenceFromXmlElement(referenceElement);
			referenceManager.addReference(reference);
		}
		
		return referenceManager;
	}

}

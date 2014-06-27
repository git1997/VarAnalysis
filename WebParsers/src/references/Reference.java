package references;

import java.util.ArrayList;
import java.util.Comparator;

import logging.MyLevel;
import logging.MyLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import constraints.Constraint;
import constraints.TrueConstraint;
import deprecated.WebEntitiesConfig;

import sourcetracing.Location;

import entities.Entity;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Reference {

	private String name;						// The name of this reference
	private ArrayList<Character> characters;	// The characters that make up this reference
	
	private Entity entity = null;				// The entity that this reference declares or refers to

	private Constraint constraint = null;		// The path constraints of this reference
	
	/**
	 * Constructor.
	 * @param name
	 * @param location
	 */
	public Reference(String name, Location location) {
		ArrayList<Character> characters = new ArrayList<Character>();
		for (int i = 0; i < name.length(); i++) {
			String filePath = location.getLocationAtOffset(i).getFilePath();
			int position = location.getLocationAtOffset(i).getPosition();
			characters.add(new Character(filePath, position));
		}
		this.name = name;
		this.characters = characters;
	}
	
	/**
	 * Sets the entity of the reference. 
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Sets the constraint for the reference
	 */
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Get properties
	 */
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	public ArrayList<Character> getCharacters() {
		return new ArrayList<Character>(characters);
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public Constraint getConstraint() {
		return (constraint != null ? constraint : TrueConstraint.inst);
	}
	
	public String getFilePath() {
		return characters.get(0).getFilePath();
	}
	
	public int getPosition() {
		return characters.get(0).getPosition();
	}
	
	public String getLocationString() {
		return getFilePath() + "@" + getPosition();
	}
	
	/**
	 * Returns true if this reference has the same name and type as the other reference.
	 * Subclasses of Reference may add more conditions to determine whether two references are "the same".
	 */
	public boolean sameAs(Reference reference) {
		return getName().equals(reference.getName()) && getType().equals(reference.getType());
	}
	
	/*
	 * Supports sorting of references
	 */
	
	public static class ReferenceComparator implements Comparator<Reference> {
		
		private Comparator<Reference> firstComparator, secondComparator, thirdComparator;
		
		public ReferenceComparator(Comparator<Reference> firstComparator, Comparator<Reference> secondComparator, Comparator<Reference> thirdComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
			this.thirdComparator = thirdComparator;
		}
		
		public ReferenceComparator() {
			this(new ReferenceComparatorByFile(), new ReferenceComparatorByPosition(), new ReferenceComparatorByName());
		}

		@Override
		public int compare(Reference ref1, Reference ref2) {
			int result = firstComparator.compare(ref1, ref2);
			if (result != 0)
				return result;
			
			result = secondComparator.compare(ref1, ref2);
			if (result != 0)
				return result;
			
			result = thirdComparator.compare(ref1, ref2);
			return result;
		}
	}
	
	public static class ReferenceComparatorByName implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getName().compareTo(ref2.getName());
		}
	}
	
	public static class ReferenceComparatorByFile implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getFilePath().compareTo(ref2.getFilePath());
		}
	}
	
	public static class ReferenceComparatorByPosition implements Comparator<Reference> {

		@Override
		public int compare(Reference ref1, Reference ref2) {
			return ref1.getPosition() - ref2.getPosition();
		}
	}
	
	/*
	 * Provides formatting for XML
	 */
	
	/**
	 * Prints the reference to an XML element.
	 */
	public Element printToXmlElement(Document document) {
		Element referenceElement = document.createElement(WebEntitiesConfig.XML_REFERENCE);
		referenceElement.setAttribute(WebEntitiesConfig.XML_REF_TYPE, this.getType());
		
		writePropertiesToXmlElement(document, referenceElement);
		
		return referenceElement;
	}
	
	/**
	 * Writes properties of a reference to an XML element.
	 * This method should be overridden by subclasses of Reference.
	 */
	public void writePropertiesToXmlElement(Document document, Element referenceElement) {
		referenceElement.setAttribute(WebEntitiesConfig.XML_REF_NAME, this.getName());
		
		// To save disk space, see if the characters are consecutive. If they are, print the
		// file path and location of the first character of the reference only, then the 
		// file paths and locations of all the other characters can be inferred accordingly.
		boolean printCharacters = false;
		Character firstCharacter = characters.get(0);
		for (int i = 1; i < characters.size(); i++) {
			if (!characters.get(i).getFilePath().equals(firstCharacter.getFilePath()) || (characters.get(i).getPosition() != firstCharacter.getPosition() + i)) {
				printCharacters = true;
				break;
			}
		}
		
		if (printCharacters) {
			for (Character character : characters)
				referenceElement.appendChild(character.printToXmlElement(document));		
		}
		else {
			// Also to save disk space, the file path of an reference is only printed out when it is different than its entity's file path.
			if (getEntity() == null || !this.getFilePath().equals(this.getEntity().getFilePath())) {
				referenceElement.setAttribute(WebEntitiesConfig.XML_FILE_PATH, this.getFilePath());
			}
			referenceElement.setAttribute(WebEntitiesConfig.XML_POSITION, String.valueOf(this.getPosition()));
		}
		
		if (constraint != null) {
			//Element constraintElement = document.createElement(WebEntitiesConfig.XML_CONSTRAINT);
			//referenceElement.appendChild(constraintElement);
			//constraintElement.appendChild(getConstraint().printToXmlElement(document));
		}
	}
	
	/**
	 * Reads the reference from an XML element.
	 */
	public static Reference readReferenceFromXmlElement(Element referenceElement) {
		String type = referenceElement.getAttribute(WebEntitiesConfig.XML_REF_TYPE);
		Reference reference = createInstance(type);
		
		if (reference != null) {
			reference.readPropertiesFromXmlElement(referenceElement);
		}
		return reference;
	}
	
	/**
	 * Creates a new Reference instance
	 */
	private static Reference createInstance(String type) {
		Reference reference;
		
		if (type.equals("HtmlFormDecl"))
			reference = new HtmlFormDecl(null, null);
		
		else if (type.equals("HtmlIdDecl"))
			reference = new HtmlIdDecl(null, null);
		
		else if (type.equals("HtmlInputDecl"))
			reference = new HtmlInputDecl(null, null, null, null);
		
		else if (type.equals("HtmlQueryDecl"))
			reference = new HtmlQueryDecl(null, null);
		
		else if (type.equals("JsFunctionCall"))
			reference = new JsFunctionCall(null, null);
		
		else if (type.equals("JsFunctionDecl"))
			reference = new JsFunctionDecl(null, null);
		
		else if (type.equals("JsRefToHtmlForm"))
			reference = new JsRefToHtmlForm(null, null);
		
		else if (type.equals("JsRefToHtmlId"))
			reference = new JsRefToHtmlId(null, null);
		
		else if (type.equals("JsRefToHtmlInput"))
			reference = new JsRefToHtmlInput(null, null, null);
		
		else if (type.equals("PhpRefToHtmlEntity"))
			reference = new PhpRefToHtmlEntity(null, null, null);
		
		else if (type.equals("PhpRefToSqlTableColumn"))
			reference = new PhpRefToSqlTableColumn(null, null, null);
		
		else if (type.equals("SqlTableColumnDecl"))
			reference = new SqlTableColumnDecl(null, null, null);
		
		else {
			reference = null;
			MyLogger.log(MyLevel.USER_EXCEPTION, "In Reference.java: Undefined type of reference: " + type);
		}
		
		return reference;
	}
	
	/**
	 * Reads properties of a reference from an XML element.
	 * This method should be overridden by subclasses of Reference.
	 */
	public void readPropertiesFromXmlElement(Element referenceElement) {
		String name = referenceElement.getAttribute(WebEntitiesConfig.XML_REF_NAME);
		
		ArrayList<Character> characters = new ArrayList<Character>();
		if (referenceElement.hasChildNodes()) {
			NodeList nodeList = referenceElement.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element characterElement = (Element) nodeList.item(i);
				characters.add(Character.readCharacterFromXmlElement(characterElement));
			}
		}
		else {
			String filePath;
			if (referenceElement.hasAttribute(WebEntitiesConfig.XML_FILE_PATH))
				filePath = referenceElement.getAttribute(WebEntitiesConfig.XML_FILE_PATH);
			else
				filePath = ((Element) referenceElement.getParentNode()).getAttribute(WebEntitiesConfig.XML_FILE_PATH);
			int position = Integer.valueOf(referenceElement.getAttribute(WebEntitiesConfig.XML_POSITION));
			for (int i = 0; i < name.length(); i++) {
				characters.add(new Character(filePath, position + i));
			}
		}
	}

}

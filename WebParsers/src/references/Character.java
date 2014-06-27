package references;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import deprecated.WebEntitiesConfig;

/**
 * 
 * @author HUNG
 *
 */
public class Character {
	
	protected String filePath;		// The file that contains this character	
	protected int position;			// The position of this character in the file
	
	/**
	 * 
	 * @param filePath
	 * @param position
	 */
	public Character(String filePath, int position) {
		this.filePath = filePath;
		this.position = position;
	}
	
	/*
	 * Get properties
	 */
	
	public String getFilePath() {
		return filePath;
	}
	
	public int getPosition() {
		return position;
	}

	/**
	 * Prints the character to XML format
	 */
	public Element printToXmlElement(Document document) {
		Element characterElement = document.createElement(WebEntitiesConfig.XML_CHARACTER);
		characterElement.setAttribute(WebEntitiesConfig.XML_FILE_PATH, filePath);
		characterElement.setAttribute(WebEntitiesConfig.XML_POSITION, String.valueOf(position));
		return characterElement;
	}
	
	/**
	 * Reads the character from an XML element.
	 */
	public static Character readCharacterFromXmlElement(Element characterElement) {
		String filePath = characterElement.getAttribute(WebEntitiesConfig.XML_FILE_PATH);
		int position = Integer.parseInt(characterElement.getAttribute(WebEntitiesConfig.XML_POSITION));
		return new Character(filePath, position);
	}
	
}
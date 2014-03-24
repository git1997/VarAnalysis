package util.sourcetracing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author HUNG
 *
 */
public abstract class Location {

	/**
	 * Returns the source code location at a given offset from the current location.
	 */
	public abstract SourceCodeLocation getLocationAtOffset(int offsetPosition);
	
	/**
	 * Prints the location
	 */
	public abstract Element printToXmlFormat(Document document, int offsetPosition);
	
}

package sourcetracing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author HUNG
 *
 */
public class ScatteredLocation extends Location {

	private Location firstHalfLocation;	
	private Location secondHalfLocation;
	private int firstHalfLength = 0;
	
	/**
	 * Constructor
	 */
	public ScatteredLocation(Location firstHalfLocation, Location secondHalfLocation, int firstHalfLength) {
		this.firstHalfLocation = firstHalfLocation;
		this.secondHalfLocation = secondHalfLocation;
		this.firstHalfLength = firstHalfLength;
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#getLocation(int)
	 */
	@Override
	public SourceCodeLocation getLocationAtOffset(int offsetPosition) {
		if (offsetPosition < firstHalfLength)
			return firstHalfLocation.getLocationAtOffset(offsetPosition);
		else
			return secondHalfLocation.getLocationAtOffset(offsetPosition - firstHalfLength);
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#printToXmlFormat(org.w3c.dom.Document, int)
	 */
	@Override
	public Element printToXmlFormat(Document document, int offsetPosition) {
		if (offsetPosition < firstHalfLength)
			return firstHalfLocation.printToXmlFormat(document, offsetPosition);
		else
			return secondHalfLocation.printToXmlFormat(document, offsetPosition - firstHalfLength);
	}
	
}

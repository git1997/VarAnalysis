package sourcetracing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author HUNG
 *
 */
public class SingleLocation extends Location {
	
	private Location location;
	private int position;
	
	/**
	 * Constructor
	 */
	public SingleLocation(Location location, int position) {
		this.location = location;
		this.position = position;
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#getLocation(int)
	 */
	@Override
	public SourceCodeLocation getLocationAtOffset(int offsetPosition) {
		return location.getLocationAtOffset(position + offsetPosition);
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#printToXmlFormat(org.w3c.dom.Document, int)
	 */
	@Override
	public Element printToXmlFormat(Document document, int offsetPosition) {
		return location.printToXmlFormat(document, position + offsetPosition);
	}

}

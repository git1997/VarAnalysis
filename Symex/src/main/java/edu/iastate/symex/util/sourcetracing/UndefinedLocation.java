package edu.iastate.symex.util.sourcetracing;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author HUNG
 *
 */
public class UndefinedLocation extends SourceCodeLocation {
	
	public static UndefinedLocation inst = new UndefinedLocation();
	
	/**
	 * Private constructor.
	 */
	private UndefinedLocation() {
		super(new File("."), -1);
	}

	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#getLocation(int)
	 */
	@Override
	public SourceCodeLocation getLocationAtOffset(int offsetPosition) {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#printToXmlFormat(org.w3c.dom.Document, int)
	 */
	@Override
	public Element printToXmlFormat(Document document, int offsetPosition) {
		return super.printToXmlFormat(document, offsetPosition);
	}

}

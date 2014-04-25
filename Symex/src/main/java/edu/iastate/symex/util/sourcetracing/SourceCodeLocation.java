package edu.iastate.symex.util.sourcetracing;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class SourceCodeLocation extends Location {
	
	private File filePath;
	private int position;
	
	/**
	 * Constructor.
	 */	
	public SourceCodeLocation(File filePath, int position) {
		this.filePath = filePath;
		this.position = position;
	}
	
	/*
	 * Get properties
	 */
	
	public File getFilePath() {
		return filePath;
	}
	
	public int getPosition() {
		return position;
	}
	
	public int getLine() {
		return FileIO.getLineFromOffsetInFile(filePath, position);
	}
	
	/**
	 * Returns true if the location is undefined.
	 * @return
	 */
	public boolean isUndefined() {
		return getPosition() < 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#getLocation(int)
	 */
	@Override
	public SourceCodeLocation getLocationAtOffset(int offsetPosition) {
		return new SourceCodeLocation(filePath, position + offsetPosition);
	}
	
	/*
	 * (non-Javadoc)
	 * @see sourcetracing.Location#printToXmlFormat(org.w3c.dom.Document, int)
	 */
	@Override
	public Element printToXmlFormat(Document document, int offsetPosition) {
		Element element = document.createElement("SourceCodeLocation");
		element.setAttribute("File", filePath.getPath());
		element.setAttribute("Position", Integer.toString(position + offsetPosition));
		return element;
	}
	
}

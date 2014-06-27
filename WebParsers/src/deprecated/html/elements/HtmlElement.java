package deprecated.html.elements;

import java.util.ArrayList;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public abstract class HtmlElement {
	
	protected Location location;		// The location of the HtmlElement in the source code
	
	private HtmlElement parentElement = null;	// The parent element
	private ArrayList<HtmlElement> childElements = new ArrayList<HtmlElement>();	// The child elements
	
	/**
	 * Constructor
	 * @param location
	 */
	public HtmlElement(Location location) {
		this.location = location;
	}
	
	/*
	 * Set properties
	 */
	
	public void addChildElement(HtmlElement element) {
		if (element instanceof HtmlText && !childElements.isEmpty() && childElements.get(childElements.size() - 1) instanceof HtmlText) {
			HtmlText text1 = (HtmlText) childElements.get(childElements.size() - 1);
			HtmlText text2 = (HtmlText) element;			
			text1.addText(text2);		
		}
		else {
			childElements.add(element);
			element.setParentElement(this);
		}
	}
	
	/**
	 * Sets parentElement - Private access: Should only be called from HtmlElement.addChildElement
	 * @param element
	 */
	private void setParentElement(HtmlElement element) {
		this.parentElement = element;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the location of the HtmlElement.
	 */
	public Location getLocation() {
		return location;
	}
	
	public HtmlElement getParentElement() {
		return parentElement;
	}
	
	public ArrayList<HtmlElement> getChildElements() {
		return new ArrayList<HtmlElement>(childElements);
	}
	
	/**
	 * Used for debugging
	 */
	public abstract String print(int depth);

}

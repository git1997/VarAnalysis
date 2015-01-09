package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttribute {
	
	private String name;					// Name of the attribute
	private PositionRange location;			// The location of the attribute's name
	
	private HtmlAttributeValue value;		// The attribute's value
	
	private HtmlToken eqToken = null;		// The '=' token after the attribute name
	private HtmlToken attrValStart = null;	// The ' or " token at the start of the attribute value
	private HtmlToken attrValEnd = null; 	// The ' or " token at the end of the attribute value
	
	private HtmlElement parentElement = null;
	private Constraint constraint = Constraint.TRUE;
	
	/**
	 * Constructor
	 */
	public HtmlAttribute(String name, PositionRange location) {
		this.name = name;
		this.location = location;
		this.value = new HtmlAttributeValue();
	}
	
	/*
	 * Set properties
	 */
	
	public void addAttrValFrag(String valueFragment, PositionRange location) {
		value.addValueFragment(valueFragment, location);
	}
	
	/**
	 * Unescapes the string value of the attribute value, preserving its length
	 */
	public void unescapePreservingLength(char stringType) {
		value.unescapePreservingLength(stringType);
	}
	
	public void setEqToken(HtmlToken eqToken) {
		this.eqToken = eqToken;
	}
	
	public void setAttrValStart(HtmlToken attrValStart) {
		this.attrValStart = attrValStart;
	}
	
	public void setAttrValEnd(HtmlToken attrValEnd) {
		this.attrValEnd = attrValEnd;
	}
	
	/**
	 * Protected method. Should be called from HtmlElement.HtmlElement(HOpenTag) only
	 */
	protected void setParentElement(HtmlElement parentElement) {
		this.parentElement = parentElement;
	}
	
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	/*
	 * Get properties
	 */
	
	public String getName() {
		return name;
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	public HtmlAttributeValue getAttributeValue() {
		return value;
	}
	
	public HtmlToken getEqToken() {
		return eqToken;
	}
	
	public HtmlToken getAttrValStart() {
		return attrValStart;
	}
	
	public HtmlToken getAttrValEnd() {
		return attrValEnd;
	}
	
	public HtmlElement getParentElement() {
		return parentElement;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	/*
	 * Other methods
	 */
	
	@Override
	public HtmlAttribute clone() {
		HtmlAttribute clone = new HtmlAttribute(name, location);
		clone.value = value.clone();
		
		clone.eqToken = eqToken;
		clone.attrValStart = attrValStart;
		clone.attrValEnd = attrValEnd;
		
		clone.parentElement = parentElement;
		clone.constraint = constraint;
		
		return clone;
	}
	
	public String getStringValue() {
		return value.getStringValue();
	}
	
	public String getTrimmedLowerCaseStringValue() {
		return getStringValue().trim().toLowerCase();
	}
	
	/**
	 * Returns true if the attribute is a "name".
	 * E.g. <form name="my_form">
	 */
	public boolean isNameAttribute() {
		return getName().equals("name");
	}
	
	/**
	 * Returns true if the attribute is an "id".
	 * E.g. <div id="my_div">
	 */
	public boolean isIdAttribute() {
		return getName().equals("id");
	}
	
	/**
	 * Returns true if the attribute is a "value".
	 * E.g. <input name="my_input" value="0">
	 */
	public boolean isValueAttribute() {
		return getName().equals("value");
	}
	
	/**
	 * Returns true if the attribute contains Javascript code.
	 * E.g. <body onload="sayHello()">
	 */
	public boolean containsJavascript() {
		return getName().startsWith("on") // e.g. onclick, onsubmit, etc.
			|| (getName().equals("href") && getTrimmedLowerCaseStringValue().startsWith("javascript")); // e.g <a href="javascript: login();"></a>
	}
	
	/**
	 * Returns true if the attribute contains a query string.
	 * E.g. "<a href = "google.com?my_input1=value1&my_input2=value2/>"
	 */
	public boolean containsQueryString() {
		return  getName().equals("action")
				|| (getName().equals("href") && !getTrimmedLowerCaseStringValue().startsWith("javascript")); // Make sure it's not Javascript
	}
	
	public String toDebugString() {
		if (constraint.isTautology())
			return name + (value != null ? "=" + value.toDebugString() : "");
		else
			return "[" + constraint.toDebugString() + "] " + name + (value != null ? "=" + value.toDebugString() : "");
	}

}

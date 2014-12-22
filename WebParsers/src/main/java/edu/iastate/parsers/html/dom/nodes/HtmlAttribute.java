package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttribute extends HtmlNode {
	
	private String name;
	private HtmlAttributeValue value;
	
	private HtmlToken eqToken = null;		// The '=' token after the attribute name
	private HtmlToken attrValStart = null;	// The ' or " token at the start of the attribute value
	private HtmlToken attrValEnd = null; 	// The ' or " token at the end of the attribute value
	
	private HtmlElement parentElement = null;
	private Constraint constraint = Constraint.TRUE;
	
	/**
	 * Constructor
	 */
	public HtmlAttribute(String name, PositionRange location) {
		super(location);
		this.name = name;
		this.value = new HtmlAttributeValue();
	}
	
	/*
	 * Set properties
	 */
	
	public void addAttrValFrag(String valueFragment, PositionRange location) {
		value.addValueFragment(valueFragment, location);
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
	 * Sets parentElement - protected access: Should only be called from HtmlElement.HtmlElement(HOpenTag)
	 * @param element
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
		HtmlAttribute clonedAttribute = new HtmlAttribute(name, location);
		clonedAttribute.value = value.clone();
		
		clonedAttribute.eqToken = eqToken;
		clonedAttribute.attrValStart = attrValStart;
		clonedAttribute.attrValEnd = attrValEnd;
		
		clonedAttribute.parentElement = parentElement;
		clonedAttribute.constraint = constraint;
		
		return clonedAttribute;
	}
	
	public String getStringValue() {
		return value.getStringValue();
	}
	
	public String getTrimmedLowerCaseValue() {
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
			|| (getName().equals("href") && getTrimmedLowerCaseValue().startsWith("javascript")); // e.g <a href="javascript: login();"></a>
	}
	
	/**
	 * Returns true if the attribute contains a query string.
	 * E.g. "<a href = "google.com?my_input1=value1&my_input2=value2/>"
	 */
	public boolean containsQueryString() {
		return  getName().equals("action")
				|| (getName().equals("href") && !getTrimmedLowerCaseValue().startsWith("javascript")); // Make sure it's not Javascript
	}
	
	@Override
	public String toDebugString() {
		if (constraint.isTautology())
			return name + (value != null ? "=" + value.toDebugString() : "");
		else
			return "[" + constraint.toDebugString() + "] " + name + (value != null ? "=" + value.toDebugString() : "");
	}

}

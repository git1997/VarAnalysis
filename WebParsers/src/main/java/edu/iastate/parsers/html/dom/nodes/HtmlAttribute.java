package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttribute extends HtmlNode {
	
	private String name;
	private HtmlAttributeValue value = null;
	
	private HtmlElement parentElement = null;
	private Constraint constraint = Constraint.TRUE;
	
	/**
	 * Constructor
	 */
	public HtmlAttribute(String name, PositionRange location) {
		super(location);
		this.name = name;
	}
	
	/*
	 * Set properties
	 */
	
	public void addValueFragment(String valueFragment, PositionRange location) {
		if (value == null)
			value = new HtmlAttributeValue(valueFragment, location);
		else
			value.addValueFragment(valueFragment, location);
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
	
	public String getValue() {
		return value != null ? value.getStringValue() : "";
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
	
	public HtmlAttribute clone() {
		HtmlAttribute clonedAttribute = new HtmlAttribute(name, location);
		clonedAttribute.value = (value != null ? value.clone() : null);
		clonedAttribute.parentElement = parentElement;
		clonedAttribute.constraint = constraint;
		return clonedAttribute;
	}
	
	public String getTrimmedLowerCaseValue() {
		return getValue().trim().toLowerCase();
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

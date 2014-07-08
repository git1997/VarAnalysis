package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttribute extends HtmlNode {
	
	private HtmlElement parentElement = null; // The HtmlElement that this attribute belongs to.
	
	private Constraint constraint = Constraint.TRUE;
	
	private String name;
	
	private HtmlAttributeValue value = null;
	
	/**
	 * Constructor
	 * @param name
	 */
	public HtmlAttribute(String name, PositionRange location) {
		super(location);
		this.name = name;
	}
	
	/*
	 * Set properties
	 */
	
	public void setParentElement(HtmlElement htmlElement) {
		this.parentElement = htmlElement;
	}
	
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	
	public void addValueFragment(String valueFragment, PositionRange location) {
		if (value == null)
			value = new HtmlAttributeValue(valueFragment, location);
		else
			value.addValueFragment(valueFragment, location);
	}
	
	/*
	 * Get properties
	 */
	
	public HtmlElement getParentElement() {
		return parentElement;
	}
	
	public Constraint getConstraint() {
		return constraint;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value.getStringValue();
	}
	
	public HtmlAttributeValue getAttributeValue() {
		return value;
	}
	
	public String getTrimmedLowerCaseValue() {
		return getValue().trim().toLowerCase();
	}
	
	public HtmlAttribute clone() {
		HtmlAttribute clonedAttribute = new HtmlAttribute(name, location);
		clonedAttribute.parentElement = parentElement;
		clonedAttribute.constraint = constraint;
		clonedAttribute.value = (value != null ? value.clone() : null);
		return clonedAttribute;
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
			return name + "=" + value.toDebugString();
		else
			return "[" + constraint.toDebugString() + "] " + name + "=" + value.toDebugString();
	}

}

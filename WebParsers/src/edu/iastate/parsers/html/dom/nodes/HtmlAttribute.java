package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttribute extends HtmlNode {
	
	private HOpenTag parentTag; // The tag that this attribute belongs to.
	
	private HtmlAttributeName name;
	private HtmlAttributeValue value = null;
	
	/**
	 * Constructor
	 * @param name
	 * @param location
	 */
	public HtmlAttribute(HOpenTag parentTag, HtmlAttributeName name) {
		super(name.getLocation());
		this.parentTag = parentTag;
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
	
	/*
	 * Get properties
	 */
	
	public HOpenTag getParentTag() {
		return parentTag;
	}
	
	public String getName() {
		return name.getName();
	}
	
	public String getValue() {
		return value.getStringValue();
	}
	
	public HtmlAttributeName getAttributeName() {
		return name;
	}
	
	public HtmlAttributeValue getAttributeValue() {
		return value;
	}
	
	public String getTrimmedLowerCaseValue() {
		return (value != null ? value.getStringValue().trim().toLowerCase() : "");
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
		return name.toDebugString() + "=" + value.toDebugString();
	}

}

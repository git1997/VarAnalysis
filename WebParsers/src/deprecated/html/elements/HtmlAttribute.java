package deprecated.html.elements;

import sourcetracing.Location;
import util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttribute extends HtmlElement {
	
	private HtmlTag parentTag; // The tag that this attribute belongs to.
	
	private String name;
	private HtmlAttributeValue value = null;
	
	/**
	 * Constructor
	 * @param name
	 * @param location
	 */
	public HtmlAttribute(HtmlTag parentTag, String name, Location location) {
		super(location);
		this.parentTag = parentTag;
		this.name = name;
	}
	
	/*
	 * Set properties
	 */
	
	public void addValueFragment(String valueFragment, Location location) {
		if (value == null)
			value = new HtmlAttributeValue(valueFragment, location);
		else
			value.addValueFragment(valueFragment, location);
	}
	
	/*
	 * Get properties
	 */
	
	public HtmlTag getParentTag() {
		return parentTag;
	}
	
	public String getLowerCaseName() {
		return name.toLowerCase();
	}
	
	public HtmlAttributeValue getValue() {
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
		return getLowerCaseName().equals("name");
	}
	
	/**
	 * Returns true if the attribute is an "id".
	 * E.g. <div id="my_div">
	 */
	public boolean isIdAttribute() {
		return getLowerCaseName().equals("id");
	}
	
	/**
	 * Returns true if the attribute contains Javascript code.
	 * E.g. <body onload="sayHello()">
	 */
	public boolean containsJavascript() {
		return getLowerCaseName().startsWith("on") // e.g. onclick, onsubmit, etc.
			|| (getLowerCaseName().equals("href") && getTrimmedLowerCaseValue().startsWith("javascript")); // e.g <a href="javascript: login();"></a>
	}
	
	/**
	 * Returns true if the attribute contains a query string.
	 * E.g. "<a href = "google.com?my_input1=value1&my_input2=value2/>"
	 */
	public boolean containsQueryString() {
		return  getLowerCaseName().equals("action")
				|| (getLowerCaseName().equals("href") && !getTrimmedLowerCaseValue().startsWith("javascript")); // Make sure it's not Javascript
	}

	/*
	 * Used for debugging
	 */
	@Override
	public String print(int depth) {
		return StringUtils.getIndentedTabs(depth) + name + " = " + value + "\n";
	}
	
}

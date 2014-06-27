package deprecated.html.elements;

import java.util.ArrayList;

import sourcetracing.Location;
import util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTag extends HtmlElement {

	private String type;
	private ArrayList<HtmlAttribute> attributeList = new ArrayList<HtmlAttribute>();
	
	/**
	 * HtmlTag factory.
	 */
	public static HtmlTag createHtmlTag(String type, Location location) {
		if (type.toLowerCase().equals("form"))
			return new HtmlFormTag(type, location);
		
		else if (type.toLowerCase().equals("input") || type.toLowerCase().equals("select") || type.toLowerCase().equals("textarea"))
			return new HtmlInputTag(type, location);
		
		else if (type.toLowerCase().equals("script"))
			return new HtmlScriptTag(type, location);
		
		else
			return new HtmlTag(type, location);
	}
	
	/**
	 * Protected constructor
	 * @param type
	 */
	protected HtmlTag(String type, Location location) {
		super(location);
		this.type = type;
	}
	
	/*
	 * Set properties
	 */

	public void addAttribute(HtmlAttribute attribute) {
		attributeList.add(attribute);
	}
	
	/*
	 * Get properties
	 */
	
	public String getLowerCaseType() {
		return type.toLowerCase();
	}
	
	public ArrayList<HtmlAttribute> getAttributes() {
		return new ArrayList<HtmlAttribute>(attributeList);
	}
	
	public HtmlAttribute getLastAttribute() {
		return attributeList.get(attributeList.size() - 1);
	}
	
	public HtmlAttributeValue getAttribute(String attributeName) {
		for (HtmlAttribute attribute : attributeList)
			if (attribute.getLowerCaseName().equals(attributeName.toLowerCase()))
				return attribute.getValue();
		return null;
	}
	
	/*
	 * Used for debugging
	 */
	@Override
	public String print(int depth) {
		StringBuilder string = new StringBuilder();
		String tabs = StringUtils.getIndentedTabs(depth);
		string.append(tabs + "Tag: " + type + "\n");
		string.append(tabs + "Attributes:\n");
		for (HtmlAttribute attribute : attributeList)
			string.append(attribute.print(depth + 1));
		for (HtmlElement childElement : getChildElements())
			string.append(childElement.print(depth + 1));
		return string.toString();
	}
	
}

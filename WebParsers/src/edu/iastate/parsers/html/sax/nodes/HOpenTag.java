package edu.iastate.parsers.html.sax.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HOpenTag extends HtmlSaxNode {
	
	private String type;
	private ArrayList<HtmlAttribute> attributes;
	
	/**
	 * Constructor
	 */
	public HOpenTag(String type, PositionRange location) {
		super(location);
		this.type = type.toLowerCase();
		this.attributes = new ArrayList<HtmlAttribute>();
	}
	
	/*
	 * Set properties
	 */

	public void addAttribute(HtmlAttribute attribute) {
		attributes.add(attribute);
	}
	
	/*
	 * Get properties
	 */
	
	public String getType() {
		return type;
	}
	
	public ArrayList<HtmlAttribute> getAttributes() {
		return new ArrayList<HtmlAttribute>(attributes);
	}
	
	public HtmlAttribute getLastAttribute() {
		return attributes.get(attributes.size() - 1);
	}
	
	public HtmlAttributeValue getAttribute(String attributeName) {
		for (HtmlAttribute attribute : attributes)
			if (attribute.getName().equals(attributeName.toLowerCase()))
				return attribute.getAttributeValue();
		return null;
	}
	
	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("OpenTag: " + type + "Attributes: ");
		for (HtmlAttribute attribute : attributes)
			str.append(attribute.getName() + "=" + attribute.getValue() + "  ");
		return str.toString();
	}

}

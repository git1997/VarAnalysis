package edu.iastate.parsers.html.sax.nodes;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HOpenTag extends HtmlSaxNode {
	
	private String type;
	
	private ArrayList<HtmlAttribute> attributes = new ArrayList<HtmlAttribute>();
	
	private HtmlToken endBracket = null; // The token ">" or "/>" at the end of the tag
	
	/**
	 * Constructor
	 */
	public HOpenTag(String type, PositionRange location) {
		super(location);
		this.type = type;
	}
	
	/*
	 * Set properties
	 */

	public void addAttribute(HtmlAttribute attribute) {
		attributes.add(attribute);
	}
	
	public void removeAllAttributes() {
		attributes = new ArrayList<HtmlAttribute>();
	}
	
	public void setEndBracket(HtmlToken endBracket) {
		this.endBracket = endBracket;
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
	
	public HtmlToken getEndBracket() {
		return endBracket;
	}
	
	/*
	 * Other methods
	 */
	
	@Override
	public HOpenTag clone() {
		HOpenTag clone = new HOpenTag(type, location);
		for (HtmlAttribute attr : attributes)
			clone.addAttribute(attr.clone());
		clone.setEndBracket(endBracket);
		return clone;
	}
	
	public HtmlAttribute getAttribute(String attributeName) {
		// TODO There could be multiple attributes with the same name (under different constraints)
		for (HtmlNode attribute : attributes) {
			if (((HtmlAttribute) attribute).getName().equals(attributeName))
				return (HtmlAttribute) attribute;
		}
		return null;
	}
	
	public HtmlAttributeValue getAttributeValue(String attributeName) {
		HtmlAttribute attribute = getAttribute(attributeName);
		if (attribute != null)
			return attribute.getAttributeValue();
		else
			return null;
	}
	
	public String getAttributeStringValue(String attributeName) {
		HtmlAttribute attribute = getAttribute(attributeName);
		if (attribute != null)
			return attribute.getStringValue();
		else
			return null;
	}
	
	/**
	 * Returns the last attribute or null if attribute list is empty.
	 */
	public HtmlAttribute getLastAttributeOrNull() {
		return !attributes.isEmpty() ? attributes.get(attributes.size() - 1) : null;
	}
	
	/**
	 * Returns true if the open tag is self-closed (i.e., ending with "/>")
	 */
	public boolean isSelfClosed() {
		return (endBracket != null && endBracket.getLexeme().equals("/>"));
	}
	
	@Override
	public String toDebugString() {
		StringBuilder str = new StringBuilder();
		str.append("OpenTag: " + type);
		if (!attributes.isEmpty()) {
			for (HtmlAttribute attribute : attributes)
				str.append(", " + attribute.toDebugString());
		}
		if (isSelfClosed())
			str.append(", self-closed");
		return str.toString();
	}

}

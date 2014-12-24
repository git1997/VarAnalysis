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
	
	private ArrayList<HtmlToken> endBrackets = new ArrayList<HtmlToken>(); // The token ">" or "/>" at the end of the tag, there could be multiple endBrackets (although it rarely happens)
	
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
	
	public void removeLastAttribute() {
		attributes.remove(attributes.size() - 1);
	}
	
	public void addEndBracket(HtmlToken endBracket) {
		this.endBrackets.add(endBracket);
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
	
	public ArrayList<HtmlToken> getEndBrackets() {
		return new ArrayList<HtmlToken>(endBrackets);
	}
	
	/*
	 * Other methods
	 */
	
	public int getNumberOfAttributes() {
		return attributes.size();
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
	 * Returns true if the OpenTag is self-closed (i.e., ending with "/>")
	 */
	public boolean isSelfClosed() {
		return (!endBrackets.isEmpty() && endBrackets.get(0).equals("/>"));
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

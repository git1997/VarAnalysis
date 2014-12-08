package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.generatedlexer.Token.Type;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxParser {
	
	private ArrayList<HtmlSaxNode> parseResult = new ArrayList<HtmlSaxNode>();
	
	private HtmlSaxNode lastSaxNode = null;
	
	public ArrayList<HtmlSaxNode> getParseResult() {
		return new ArrayList<HtmlSaxNode>(parseResult);
	}
	
	public void clearParseResult() {
		parseResult = new ArrayList<HtmlSaxNode>();
	}
	
	public void setLastSaxNode(HtmlSaxNode lastSaxNode) {
		this.lastSaxNode = lastSaxNode;
	}
	
	public HtmlSaxNode getLastSaxNode() {
		return lastSaxNode;
	}
	
	public void parse(HtmlToken token) {
		String tokenValue = token.getValue();
		PositionRange tokenLocation = token.getLocation();
			
		switch (token.getType()) {
			case OpeningTag: {
				HOpenTag tag = new HOpenTag(tokenValue, tokenLocation);
				parseResult.add(tag);
				lastSaxNode = tag;
				break;
			}
			case ClosingTag: {
				HCloseTag tag = new HCloseTag(tokenValue, tokenLocation);
				parseResult.add(tag);
				lastSaxNode = tag;
				break;
			}
			case Text: {
				if (!tokenValue.trim().isEmpty()) { // Only add non-empty text
					HText text = new HText(tokenValue, tokenLocation);
					parseResult.add(text);
					lastSaxNode = text;
				}
				break;
			}
			case AttrName: {
				HtmlAttribute attribute = new HtmlAttribute(tokenValue, tokenLocation);
				HOpenTag tag = resolveLastOpenTag();
				if (tag != null)
					tag.addAttribute(attribute);
				break;
			}
			case AttrValStart:
			case AttrValFrag:
			case AttrValue: {
				HtmlAttribute attribute = resolveLastAttribute();
				if (attribute != null) {
					if (token.getType() == Type.AttrValStart)
						// This has the effect of setting the value of the attribute to not-null
						// (although it's an empty string)
						attribute.addValueFragment("", PositionRange.UNDEFINED); 
					else
						attribute.addValueFragment(tokenValue, tokenLocation);
				}
				break;
			}
			case AttrValEnd: {
				HtmlAttribute attribute = resolveLastAttribute();
				if (attribute != null) {
					HtmlAttributeValue attributeValue = attribute.getAttributeValue();
					attributeValue.unescapePreservingLength(tokenValue.charAt(0)); // Unescape the attribute value
				}
				break;
			}
		}
	}
	
	private HOpenTag resolveLastOpenTag() {
		if (lastSaxNode instanceof HOpenTag)
			return (HOpenTag) lastSaxNode;
		else
			return null;
	}
	
	private HtmlAttribute resolveLastAttribute() {
		HOpenTag tag = resolveLastOpenTag();
		if (tag != null)
			return tag.getLastAttribute();
		else
			return null;
	}
	
}

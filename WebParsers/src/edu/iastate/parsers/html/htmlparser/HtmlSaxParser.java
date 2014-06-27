package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeName;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
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
	
	public ArrayList<HtmlSaxNode> getParseResult() {
		return new ArrayList<HtmlSaxNode>(parseResult);
	}
	
	public void clearParseResult() {
		parseResult = new ArrayList<HtmlSaxNode>();
	}
	
	public void parse(HtmlToken token) {
		String tokenValue = token.getValue();
		PositionRange tokenLocation = token.getLocation();
			
		switch (token.getType()) {
			case OpeningTag: {
				parseResult.add(new HOpenTag(tokenValue, tokenLocation));
				break;
			}
			case ClosingTag: {
				parseResult.add(new HCloseTag(tokenValue, tokenLocation));
				break;
			}
			case Text: {
				parseResult.add(new HText(tokenValue, tokenLocation));
				break;
			}
			case AttrName: {
				HOpenTag tag = getLastOpenTag();
				if (tag != null) {
					HtmlAttributeName attributeName = new HtmlAttributeName(tokenValue, tokenLocation);
					HtmlAttribute attribute = new HtmlAttribute(tag, attributeName);
					tag.addAttribute(attribute);
					break;
				}
			}
			case AttrValStart: {
				HOpenTag tag = getLastOpenTag();
				if (tag != null) {
					//HtmlAttribute attribute = tag.getLastAttribute();				
					//attribute.addValueFragment("", tokenLocation.getPositionRangeAtRelativeOffset(1, 1));
					break;
				}
			}
			case AttrValFrag: {
				HOpenTag tag = getLastOpenTag();
				if (tag != null) {
					HtmlAttribute attribute = tag.getLastAttribute();				
					attribute.addValueFragment(tokenValue, tokenLocation);
					break;
				}
			}
			case AttrValEnd: {
				HOpenTag tag = getLastOpenTag();
				if (tag != null) {
					HtmlAttribute attribute = tag.getLastAttribute();				
					HtmlAttributeValue attributeValue = attribute.getAttributeValue();
					attributeValue.unescapePreservingLength(tokenValue.charAt(0)); // Unescape the attribute value
					break;
				}
			}
			case AttrValue: {
				HOpenTag tag = getLastOpenTag();
				if (tag != null) {
					HtmlAttribute attribute = tag.getLastAttribute();				
					attribute.addValueFragment(tokenValue, tokenLocation);
					break;
				}
			}
		}
	}
	
	private HOpenTag getLastOpenTag() {
		if (!parseResult.isEmpty() && parseResult.get(parseResult.size() - 1) instanceof HOpenTag)
			return (HOpenTag) parseResult.get(parseResult.size() - 1);
		else
			return null;
	}
	
}

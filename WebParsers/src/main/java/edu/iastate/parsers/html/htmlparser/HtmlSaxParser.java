package edu.iastate.parsers.html.htmlparser;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.generatedlexer.Token.Type;
import edu.iastate.parsers.html.htmlparser.SaxParserEnv.ParsingState;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxParser {
	
	/**
	 * Parses the htmlToken
	 */
	public void parse(HtmlToken htmlToken, SaxParserEnv env) {
		Type tokenType = htmlToken.getType();
		String tokenValue = htmlToken.getValue();
		PositionRange tokenLocation = htmlToken.getLocation();
		
		if (!env.isInsideOpenTag()) {
			switch (tokenType) {
				case OpenTag: {
					HOpenTag openTag = new HOpenTag(tokenValue.toLowerCase(), tokenLocation); 
					env.addHtmlSaxNode(openTag);
					env.setParsingState(ParsingState.INSIDE_OPEN_TAG);
					break;
				}
				case CloseTag: {
					HCloseTag closeTag = new HCloseTag(tokenValue.toLowerCase(), tokenLocation);
					env.addHtmlSaxNode(closeTag);
					env.setParsingState(ParsingState.OUTSIDE_TEXT);
					break;
				}
				case Text: {
					if (env.isInsideText() || !tokenValue.trim().isEmpty()) { // Ignore whitespace
						HText text = new HText(tokenValue, tokenLocation);
						env.addHtmlSaxNode(text);
						env.setParsingState(ParsingState.INSIDE_TEXT);
					}
					break;
				}
				default: {
					MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlSaxParser.java: Unexpected token " + htmlToken.toDebugString() + " when outside open tag.");
				}
			}
		}
		else {
			HOpenTag openTag = env.tryGetLastOpenTag();
			if (openTag == null)
				return;
			
			boolean modifyingLastAttribute = false; // Set to true if the token is trying to modify the last attribute of the current openTag
			switch (tokenType) {
				case AttrName: {
					HtmlAttribute attribute = new HtmlAttribute(tokenValue.toLowerCase(), tokenLocation);
					openTag.addAttribute(attribute);
					break;
				}
				case OpenTagEnd: {
					openTag.addEndBracket(htmlToken);
					env.setParsingState(ParsingState.OUTSIDE_TEXT);
					break;
				}
				default: {		
					modifyingLastAttribute = true;
				}
			}
			
			if (!modifyingLastAttribute)
				return;
			
			HtmlAttribute attribute = openTag.getLastAttributeOrNull();
			if (attribute == null) {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlSaxparser.java: Can't find last attribute of open tag.");
				return;
			}
			
			switch (tokenType) {
				case Eq:				attribute.setEqToken(htmlToken); break;
				case AttrValStart:		attribute.setAttrValStart(htmlToken); break;
				case AttrValFrag:		attribute.addAttrValFrag(tokenValue, tokenLocation); break;
				case AttrValEnd: {
										attribute.setAttrValEnd(htmlToken);
										attribute.unescapePreservingLength(tokenValue.charAt(0)); // Unescape the attribute value
										break;
				}
				case AttrValue:			attribute.addAttrValFrag(tokenValue, tokenLocation); break;
				default:				MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlSaxParser.java: Unexpected token " + htmlToken.toDebugString() + " when inside open tag.");
			}
		}
	}
	
}

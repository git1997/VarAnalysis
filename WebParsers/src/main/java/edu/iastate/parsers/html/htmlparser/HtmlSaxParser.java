package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.generatedlexer.Token.Type;
import edu.iastate.parsers.html.htmlparser.HtmlSaxParser.ParserState.State;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
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
	
	private ParserState parserState;

	private ArrayList<HtmlSaxNode> parseResult;
	
	/**
	 * Constructor
	 */
	public HtmlSaxParser() {
		this.parserState = new ParserState(ParserState.State.OUTSIDE_OPEN_TAG, null);
		this.parseResult = new ArrayList<HtmlSaxNode>();
	}
	
	/*
	 * Parser state
	 */
	
	public void changeParsingState(State parsingState) {
		parserState.parsingState = parsingState;
	}
	
	public void setLastSaxNode(HtmlSaxNode lastSaxNode) {
		parserState.lastSaxNode = lastSaxNode;
	}
	
	public State getParsingState() {
		return parserState.parsingState;
	}
	
	public HtmlSaxNode getLastSaxNode() {
		return parserState.lastSaxNode;
	}
	
	public ParserState saveParserState() {
		return new ParserState(getParsingState(), getLastSaxNode());
	}
	
	public void restoreParserState(ParserState parserState) {
		this.parserState = new ParserState(getParsingState(), getLastSaxNode());
	}
	
	public boolean isInsideOpenTag() {
		return getParsingState() == State.INSIDE_OPEN_TAG;
	}
	
	/*
	 * Parse result
	 */
	
	public ArrayList<HtmlSaxNode> getParseResult() {
		return new ArrayList<HtmlSaxNode>(parseResult);
	}
	
	public void clearParseResult() {
		parseResult = new ArrayList<HtmlSaxNode>();
	}
	
	/*
	 * Parsing
	 */
	
	private void foundSaxNode(HtmlSaxNode saxNode) {
		parseResult.add(saxNode);
		setLastSaxNode(saxNode);
	}
	
	private void changeToInsideOpenTagState(HOpenTag openTag) {
		changeParsingState(ParserState.State.INSIDE_OPEN_TAG);
	}
	
	private void changeToOutsideOpenTagState() {
		changeParsingState(ParserState.State.OUTSIDE_OPEN_TAG);
	}
	
	private void reportUnexpectedTokenError(String expected, String actual) {
		MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlSaxParser.java: expecting " + expected + " but found " + actual);
	}
	
	/**
	 * Parses the HTML token
	 */
	public void parse(HtmlToken token) {
		Type tokenType = token.getType();
		String tokenValue = token.getValue();
		PositionRange tokenLocation = token.getLocation();
		
		switch (getParsingState()) {
			case OUTSIDE_OPEN_TAG: {
				switch (tokenType) {
					case OpenTag: {
						HOpenTag openTag = new HOpenTag(tokenValue.toLowerCase(), tokenLocation);
						foundSaxNode(openTag);
						changeToInsideOpenTagState(openTag);
						return;
					}
					case CloseTag: {
						HCloseTag closeTag = new HCloseTag(tokenValue.toLowerCase(), tokenLocation);
						foundSaxNode(closeTag);
						return;
					}
					case Text: {
						if (getLastSaxNode() instanceof HText || !tokenValue.trim().isEmpty()) { // Ignore whitespace
							HText text = new HText(tokenValue, tokenLocation);
							foundSaxNode(text);
						}
						return;
					}
					default: {
						reportUnexpectedTokenError("OpenTag/CloseTag/Text", tokenType.toString());
						return;
					}
				}
			}
			case INSIDE_OPEN_TAG: {
				HOpenTag tag = (HOpenTag) getLastSaxNode();
				
				if (tokenType == Type.AttrName) {
					HtmlAttribute attribute = new HtmlAttribute(tokenValue.toLowerCase(), tokenLocation);
					tag.addAttribute(attribute);
				}
				else if (tokenType == Type.OpenTagEnd) {
					tag.setEndBracket(token);
					changeToOutsideOpenTagState();
				}
				else {
					HtmlAttribute attribute = tag.getLastAttributeOrNull();
					if (attribute == null) {
						reportUnexpectedTokenError("HtmlAttribute", "null");
						return;
					}
					
					switch (tokenType) {
						case Eq:				attribute.setEqToken(token); return;
						case AttrValStart:		attribute.setAttrValStart(token); return;
						case AttrValFrag:		attribute.addAttrValFrag(tokenValue, tokenLocation); return;
						case AttrValEnd:{
												attribute.setAttrValEnd(token);
												attribute.getAttributeValue().unescapePreservingLength(tokenValue.charAt(0)); // Unescape the attribute value
												return;
						}
						case AttrValue:			attribute.addAttrValFrag(tokenValue, tokenLocation); return;
						default:				reportUnexpectedTokenError("Eq/AttrValStart/AttrValFrag/AttrValEnd/AttrValue", tokenType.toString());	return;
					}
				}
			}
		}
	}
	
	/**
	 * Represents the state of the parser
	 */
	public static class ParserState {

		public static enum State {
			OUTSIDE_OPEN_TAG, INSIDE_OPEN_TAG,
		}
		
		private State parsingState;
		private HtmlSaxNode lastSaxNode;
		
		public ParserState(State parsingState, HtmlSaxNode lastSaxNode) {
			this.parsingState = parsingState;
			this.lastSaxNode = lastSaxNode;
		}
		
		public void changeParsingState(State parsingState) {
			this.parsingState = parsingState;
		}
		
		public void setLastSaxNode(HtmlSaxNode lastSaxNode) {
			this.lastSaxNode = lastSaxNode;
		}
		
		public State getParsingState() {
			return parsingState;
		}
		
		public HtmlSaxNode getLastSaxNode() {
			return lastSaxNode;
		}
		
	}
	
}

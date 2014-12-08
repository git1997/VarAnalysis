package edu.iastate.parsers.html.generatedlexer;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlToken {

	private Token token;
	private PositionRange location;
	
	public HtmlToken(Token token, PositionRange location) {
		this.token = token;
		this.location = location;
	}
	
	public Token.Type getType() {
		return token.getType();
	}
	
	public String getValue() {
		return token.getValue();
	}
	
	public PositionRange getLocation() {
		return location;
	}
	
	/**
	 * Used for debugging
	 */
	public String toDebugString() {
		return getType() + ": " + getValue();
	}
	
}

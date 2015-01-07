package edu.iastate.parsers.html.generatedlexer;

import edu.iastate.parsers.html.generatedlexer.Token.Type;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 * Wrapper class for Token.
 */
public class HtmlToken {
	
	private Token token;
	private PositionRange location;
	
	/**
	 * Constructor
	 */
	public HtmlToken(Token token, PositionRange location) {
		this.token = token;
		this.location = location;
	}
	
	/*
	 * Get properties
	 */
	
	public Type getType() {
		return token.getType();
	}
	
	public String getLexeme() {
		return token.getLexeme();
	}
	
	public PositionRange getLocation() {
		return location;
	}

	/**
	 * Used for debugging
	 */
	public String toDebugString() {
		return token.toDebugString();
	}
	
}
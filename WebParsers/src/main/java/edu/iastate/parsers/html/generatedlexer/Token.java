package edu.iastate.parsers.html.generatedlexer;

/**
 * 
 * @author HUNG
 *
 */
public class Token {
	
	public enum Type {
		OpenTag, AttrName, AttrValStart, AttrValFrag, AttrValEnd, AttrValue, OpenTagEnd, OpenTagSelfClosed, CloseTag, Text
	}
	
	private Type type;
	private String lexeme;
	private int offset;
	
	private String value; // Sometimes value is different than lexeme (e.g., an OpeningTag with lexeme = '<form' has value = 'form')
	
	/**
	 * Constructor
	 */
	public Token(Type type, String lexeme, int offset, String value) {
		this.type = type;
		this.lexeme = lexeme;
		this.offset = offset;
		this.value = value;
	}
	
	/**
	 * Constructor
	 */
	public Token(Type type, String lexeme, int offset) {
		this(type, lexeme, offset, lexeme);
	}
	
	/*
	 * Get properties
	 */
	
	public Type getType() {
		return type;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String getValue() {
		return value;
	}

	/**
	 * Used for debugging
	 */
	public String toDebugString() {
		return type + ": " + lexeme;
	}
	
}
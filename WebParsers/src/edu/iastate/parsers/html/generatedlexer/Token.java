package edu.iastate.parsers.html.generatedlexer;

/**
 * 
 * @author HUNG
 *
 */
public class Token {
	
	public enum Type {
		OpeningTag, ClosingTag, AttrName, AttrValStart, AttrValFrag, AttrValEnd, AttrValue, Text
	}
	
	private Type type;
	private String lexeme;
	private int position;
	
	private String value;	// value is extracted from lexeme (e.g. lexeme = '<form>' => value = 'form')
	
	/**
	 * Constructor
	 */
	public Token(Type type, String lexeme, int position, String value) {
		this.type = type;
		this.lexeme = lexeme;
		this.position = position;
		this.value = value;
	}
	
	/**
	 * Constructor
	 */
	public Token(Type type, String lexeme, int position) {
		this(type, lexeme, position, lexeme);
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
	
	public int getPosition() {
		return position;
	}
	
	public String getValue() {
		return value;
	}

	public String toString() {
		return "Type: " + type + "\tLexeme: " + lexeme + "\tPosition: " + position;
	}
	
}
package edu.iastate.parsers.html.generatedlexer;

/**
 * 
 * @author HUNG
 *
 */
public class Token {
	
	public enum Type {
		OpenTag, AttrName, Eq, AttrValStart, AttrValFrag, AttrValEnd, AttrValue, OpenTagEnd, CloseTag, Text
	}
	
	private Type type;
	private String lexeme;
	private int offset;
	
	/**
	 * Constructor
	 */
	public Token(Type type, String lexeme, int offset) {
		this.type = type;
		this.lexeme = lexeme;
		this.offset = offset;
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
	
	/**
	 * Used for debugging
	 */
	public String toDebugString() {
		return type + ": " + lexeme;
	}
	
}
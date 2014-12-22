package edu.iastate.parsers.html.htmlparser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.generatedlexer.Lexer;
import edu.iastate.parsers.html.generatedlexer.Token;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.RelativeRange;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlLexer {
	
	private LexerState lexerState;

	private ArrayList<HtmlToken> lexResult;
	
	/**
	 * Constructor
	 */
	public HtmlLexer() {
		this.lexerState = new LexerState(Lexer.YYINITIAL, "");
		this.lexResult = new ArrayList<HtmlToken>();
	}
	
	/*
	 * Lexer state
	 */
	
	public LexerState saveLexerState() {
		return new LexerState(lexerState.getLexicalState(), lexerState.getCurrentOpenTag());
	}
	
	public void restoreLexerState(LexerState lexerState) {
		this.lexerState = new LexerState(lexerState.getLexicalState(), lexerState.getCurrentOpenTag());
	}
	
	/*
	 * Lex result
	 */
	
	public ArrayList<HtmlToken> getLexResult() {
		return new ArrayList<HtmlToken>(lexResult);
	}
	
	public void clearLexResult() {
		lexResult = new ArrayList<HtmlToken>();
	}
	
	/**
	 * Lexes the htmlCode
	 */
	public void lex(String htmlCode, PositionRange htmlLocation) {
		Lexer lexer = new Lexer(new StringReader(htmlCode));
		
		// Set the lexer state
		lexer.yybegin(lexerState.getLexicalState()); 
		lexer.setCurrentOpenTag(lexerState.getCurrentOpenTag());
		
		while (true) {
			try {
				Token token = lexer.nextToken();
				if (token == null)
					break;
				
				PositionRange location = new RelativeRange(htmlLocation, token.getOffset(), token.getLexeme().length());
				lexResult.add(new HtmlToken(token, location));			
			} catch (IOException e) {
				MyLogger.log(MyLevel.JAVA_EXCEPTION, e.getStackTrace().toString());
			}			
		}
		
		// Update the lexer state
		lexerState = new LexerState(lexer.yystate(), lexer.getCurrentOpenTag());
	}
	
	/**
	 * Represents the state of the lexer
	 */
	public static class LexerState {
		
		private int lexicalState;
		private String currentOpenTag;
		
		public LexerState(int lexicalState, String currentOpenTag) {
			this.lexicalState = lexicalState;
			this.currentOpenTag = currentOpenTag;
		}
		
		public int getLexicalState() {
			return lexicalState;
		}
		
		public String getCurrentOpenTag() {
			return currentOpenTag;
		}
		
	}
	
}

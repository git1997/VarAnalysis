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
	
	private LexicalState lexicalState = new LexicalState(Lexer.YYINITIAL, "");

	private ArrayList<HtmlToken> lexResult = new ArrayList<HtmlToken>();
	
	public LexicalState saveLexicalState() {
		return new LexicalState(lexicalState.getLexicalState(), lexicalState.getCurrentOpeningTag());
	}
	
	public void restoreLexicalState(LexicalState lexicalState) {
		this.lexicalState = new LexicalState(lexicalState.getLexicalState(), lexicalState.getCurrentOpeningTag());
	}
	
	public ArrayList<HtmlToken> getLexResult() {
		return new ArrayList<HtmlToken>(lexResult);
	}
	
	public void clearLexResult() {
		lexResult = new ArrayList<HtmlToken>();
	}
	
	public void lex(String htmlCode, PositionRange htmlLocation) {
		Lexer lexer = new Lexer(new StringReader(htmlCode));
		
		// Set the lexical state
		lexer.yybegin(lexicalState.getLexicalState()); 
		lexer.setCurrentOpenTag(lexicalState.getCurrentOpeningTag());
		
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
		
		// Update the lexical state
		lexicalState.setLexicalState(lexer.yystate());
		lexicalState.setCurrentOpeningTag(lexer.getCurrentOpenTag());
	}
	
	public class LexicalState {
		
		public int lexicalState;
		public String currentOpeningTag;
		
		public LexicalState(int lexicalState, String currentOpeningTag) {
			this.lexicalState = lexicalState;
			this.currentOpeningTag = currentOpeningTag;
		}
		
		public void setLexicalState(int lexicalState) {
			this.lexicalState = lexicalState;
		}

		public void setCurrentOpeningTag(String currentOpeningTag) {
			this.currentOpeningTag = currentOpeningTag;
		}
		
		public int getLexicalState() {
			return lexicalState;
		}
		
		public String getCurrentOpeningTag() {
			return currentOpeningTag;
		}
		
	}
	
}

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
	
	private int lexicalState = Lexer.YYINITIAL;

	private ArrayList<HtmlToken> lexResult = new ArrayList<HtmlToken>();
	
	public HtmlLexer() {
		System.out.print("");
	}
	
	public int saveLexicalState() {
		return lexicalState;
	}
	
	public void restoreLexicalState(int lexicalState) {
		this.lexicalState = lexicalState;
	}
	
	public ArrayList<HtmlToken> getLexResult() {
		return new ArrayList<HtmlToken>(lexResult);
	}
	
	public void clearLexResult() {
		lexResult = new ArrayList<HtmlToken>();
	}
	
	public void lex(String htmlCode, PositionRange htmlLocation) {
		Lexer lexer = new Lexer(new StringReader(htmlCode));
		lexer.yybegin(lexicalState); // Set the lexical state
		while (true) {
			try {
				Token nextToken = lexer.nextToken();
				if (nextToken == null)
					break;
				
				// Adjust position
				PositionRange location = new RelativeRange(htmlLocation, nextToken.getPosition(), nextToken.getLexeme().length());
				lexResult.add(new HtmlToken(nextToken, location));				
			} catch (IOException e) {
				MyLogger.log(MyLevel.JAVA_EXCEPTION, e.getStackTrace().toString());
			}			
		}
		lexicalState = lexer.yystate(); // Update the lexical state
	}
	
}

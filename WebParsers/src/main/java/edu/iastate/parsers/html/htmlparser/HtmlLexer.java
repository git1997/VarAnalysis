package edu.iastate.parsers.html.htmlparser;

import java.io.IOException;
import java.io.StringReader;

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
	
	/**
	 * Lexes the htmlCode
	 */
	public void lex(String htmlCode, PositionRange htmlLocation, LexerEnv env) {
		Lexer lexer = new Lexer(new StringReader(htmlCode));
		
		// Set the lexer state
		lexer.yybegin(env.getLexcicalState()); 
		lexer.setCurrentOpenTag(env.getCurrentOpenTag());
		
		while (true) {
			Token token = null;
			try {
				token = lexer.nextToken();
			} catch (IOException e) {
				MyLogger.log(MyLevel.JAVA_EXCEPTION, "Error during lexing: " + e.getStackTrace().toString());
			}
			if (token == null)
				break;
				
			PositionRange location = new RelativeRange(htmlLocation, token.getOffset(), token.getLexeme().length());
			HtmlToken htmlToken = new HtmlToken(token, location);
			env.addLexResult(htmlToken);
		}
		
		// Update the lexer state
		env.setLexicalState(lexer.yystate());
		env.setCurrentOpenTag(lexer.getCurrentOpenTag());
	}
	
}

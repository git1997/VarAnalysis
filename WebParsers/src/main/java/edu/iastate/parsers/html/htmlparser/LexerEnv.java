package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListFactory;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.generatedlexer.Lexer;
import edu.iastate.symex.constraints.Constraint;

/**
 * 
 * @author HUNG
 *
 */
public class LexerEnv {
	
	private static CondListFactory<HtmlToken> condListFactory = new CondListFactory<HtmlToken>();

	/*
	 * The outerScopeEnv
	 */
	private LexerEnv outerScopeEnv;
	
	/*
	 * State of the lexer
	 */
	private int lexicalState;
	private String currentOpenTag;

	/*
	 * The lex result
	 */
	private ArrayList<CondList<HtmlToken>> lexResult;
	
	/**
	 * Constructor
	 */
	public LexerEnv() {
		this.outerScopeEnv = null;
		this.lexicalState = Lexer.YYINITIAL;
		this.currentOpenTag = null;
		this.lexResult = new ArrayList<CondList<HtmlToken>>();
	}
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public LexerEnv(LexerEnv outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		this.lexicalState = outerScopeEnv.lexicalState;
		this.currentOpenTag = outerScopeEnv.currentOpenTag;
		this.lexResult = new ArrayList<CondList<HtmlToken>>();
	}
	
	/**
	 * Returns the outerScopeEnv
	 */
	public LexerEnv getOuterScopeEnv() {
		return outerScopeEnv;
	}
	
	/**
	 * Returns the lexResult
	 */
	public CondList<HtmlToken> getLexResult() {
		return condListFactory.createCompactConcat(new ArrayList<CondList<HtmlToken>>(lexResult));
	}
	
	/*
	 * Protected methods, called by HtmlLexer only.
	 */
	
	protected void setLexicalState(int lexicalState) {
		this.lexicalState = lexicalState;
	}
	
	protected void setCurrentOpenTag(String currentOpenTag) {
		this.currentOpenTag = currentOpenTag;
	}
	
	protected void addLexResult(HtmlToken htmlToken) {
		lexResult.add(condListFactory.createCondListItem(htmlToken));
	}
	
	protected int getLexcicalState() {
		return lexicalState;
	}
	
	protected String getCurrentOpenTag() {
		return currentOpenTag;
	}
	
	/**
	 * Updates the current Env after lexing two branches
	 */
	public void updateAfterLexingBranches(Constraint constraint, CondList<HtmlToken> lexResultInTrueBranch, CondList<HtmlToken> lexResultInFalseBranch) {
		CondList<HtmlToken> select = condListFactory.createCompactSelect(constraint, lexResultInTrueBranch, lexResultInFalseBranch);
		lexResult.add(select);
	}
	
}

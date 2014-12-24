package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListFactory;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.generatedlexer.Lexer;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

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
	
	protected int getLexcicalState() {
		return lexicalState;
	}
	
	protected String getCurrentOpenTag() {
		return currentOpenTag;
	}
	
	protected void addHtmlToken(HtmlToken htmlToken) {
		lexResult.add(condListFactory.createCondListItem(htmlToken));
	}
	
	/**
	 * Updates the current Env after lexing two branches
	 */
	public void updateAfterLexingBranches(Constraint constraint, LexerEnv trueBranchEnv, LexerEnv falseBranchEnv) {
		/*
		 * Combine lexResults in the two branches
		 */
		CondList<HtmlToken> lexResultInTrueBranch = trueBranchEnv.getLexResult();
		CondList<HtmlToken> lexResultInFalseBranch = falseBranchEnv.getLexResult();
		CondList<HtmlToken> select = condListFactory.createCompactSelect(constraint, lexResultInTrueBranch, lexResultInFalseBranch);
		lexResult.add(select);
		
		/*
		 * Check the state
		 */
		if (trueBranchEnv.getLexcicalState() != falseBranchEnv.getLexcicalState()) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In LexerEnv.java: Lexer ends up in different states after branches: " + Lexer.getState(trueBranchEnv.getLexcicalState()) + " vs. " + Lexer.getState(falseBranchEnv.getLexcicalState()));
		}
		
		if (trueBranchEnv.getCurrentOpenTag() != null && falseBranchEnv.getCurrentOpenTag() == null
				|| trueBranchEnv.getCurrentOpenTag() == null && falseBranchEnv.getCurrentOpenTag() != null
				|| trueBranchEnv.getCurrentOpenTag() != null && falseBranchEnv.getCurrentOpenTag() != null && !trueBranchEnv.getCurrentOpenTag().equals(falseBranchEnv.getCurrentOpenTag())) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In LexerEnv.java: Lexer ends up in different openTags after branches.");
		}
		
		// Use the state of the false branch since it's more likely to be a normal state
		setLexicalState(falseBranchEnv.getLexcicalState());
		setCurrentOpenTag(falseBranchEnv.getCurrentOpenTag());
	}
	
}

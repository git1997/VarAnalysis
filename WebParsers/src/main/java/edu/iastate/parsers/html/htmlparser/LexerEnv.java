package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListEmpty;
import edu.iastate.parsers.conditional.CondListFactory;
import edu.iastate.parsers.conditional.CondListItem;
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
	private ArrayList<CondList<HtmlToken>> lexResult = new ArrayList<CondList<HtmlToken>>(); 
	
	/**
	 * Constructor
	 */
	public LexerEnv() {
		this.outerScopeEnv = null;
		this.lexicalState = Lexer.YYINITIAL;
		this.currentOpenTag = null;
	}
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public LexerEnv(LexerEnv outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		this.lexicalState = outerScopeEnv.lexicalState;
		this.currentOpenTag = outerScopeEnv.currentOpenTag;
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
		 * Check the state
		 */
		if (trueBranchEnv.lexicalState != falseBranchEnv.lexicalState) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In LexerEnv.java: Lexer ends up in different states after branches: " +
														"before=" + Lexer.getState(this.lexicalState) + "; true=" + Lexer.getState(trueBranchEnv.lexicalState)  + " vs. false=" + Lexer.getState(falseBranchEnv.lexicalState) +
														"| Last token: " +
														"before=" + getLastTokenInLexResult(this.lexResult) + "; true=" + getLastTokenInLexResult(trueBranchEnv.lexResult) + " vs. false=" + getLastTokenInLexResult(falseBranchEnv.lexResult));
		}
		if (trueBranchEnv.currentOpenTag != null && falseBranchEnv.currentOpenTag == null
				|| trueBranchEnv.currentOpenTag == null && falseBranchEnv.currentOpenTag!= null
				|| trueBranchEnv.currentOpenTag != null && falseBranchEnv.currentOpenTag != null && !trueBranchEnv.currentOpenTag.equals(falseBranchEnv.currentOpenTag)) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In LexerEnv.java: Lexer ends up in different openTags after branches: " +
															"before=" + (this.currentOpenTag != null ? this.currentOpenTag : "null") + "; " +
															"true=" + (trueBranchEnv.currentOpenTag != null ? trueBranchEnv.currentOpenTag : "null") +
															" vs. false=" + (falseBranchEnv.currentOpenTag != null ? falseBranchEnv.currentOpenTag : "null"));
		}
		
		/*
		 * Combine lexResults in the two branches
		 */
		CondList<HtmlToken> lexResultInTrueBranch = trueBranchEnv.getLexResult();
		CondList<HtmlToken> lexResultInFalseBranch = falseBranchEnv.getLexResult();
		CondList<HtmlToken> select = condListFactory.createCompactSelect(constraint, lexResultInTrueBranch, lexResultInFalseBranch);
		if (!(select instanceof CondListEmpty<?>)) {
			lexResult.add(select);
		}
		
		/*
		 * Update the state
		 * Use the state of the false branch since it's more likely to be a normal state
		 */
		setLexicalState(falseBranchEnv.getLexcicalState());
		setCurrentOpenTag(falseBranchEnv.getCurrentOpenTag());
	}
	
	/*
	 * Utility methods
	 */
	
	/**
	 * Returns a string describing the last HtmlToken in a lexResult (for debugging only)
	 */
	private String getLastTokenInLexResult(ArrayList<CondList<HtmlToken>> lexResult) {
		if (lexResult.isEmpty())
			return "(empty)";
		
		CondList<HtmlToken> lastItem  = lexResult.get(lexResult.size() - 1);
		if (lastItem instanceof CondListItem<?>) {
			HtmlToken lastToken = ((CondListItem<HtmlToken>) lastItem).getItem();
			return lastToken.getLexeme() + " @ " + lastToken.getLocation().getStartPosition().toDebugString();
		}
		else
			return lastItem.getClass().getSimpleName();
	}
	
}

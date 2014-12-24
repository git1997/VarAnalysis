package edu.iastate.parsers.html.htmlparser;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelToHtmlTokens extends DataModelVisitor {
	
	private HtmlLexer lexer = new HtmlLexer();
	private LexerEnv env = new LexerEnv();
	
	/**
	 * Lex a DataModel and return a conditional list of tokens
	 */
	public CondList<HtmlToken> lex(DataModel dataModel) {
		dataModel.getRoot().accept(this);
		return env.getLexResult();
	}
	
	/**
	 * Lex a SymbolicNode
	 */
	@Override
	public boolean visitSymbolicNode(SymbolicNode symbolicNode) {
		// Use '111..' to replace the symbolic value.
		// Make its length equal to the length of the symbolic value's location to support location tracing.
		int length = symbolicNode.getLocation().getLength();
		String symbolicValue = "";
		for (int i = 0; i < length; i++)
			symbolicValue = symbolicValue + "1";
		
		LiteralNode literalNode = DataNodeFactory.createLiteralNode(symbolicValue, symbolicNode.getLocation());
		literalNode.accept(this);
		
		return true;
	}
	
	/**
	 * Lex a SelectNode
	 */
	@Override
	public boolean visitSelectNode(SelectNode selectNode) {
		/*
		 * Lex the true branch
		 */
		LexerEnv trueBranchEnv = new LexerEnv(env);
		env = trueBranchEnv;
		selectNode.getNodeInTrueBranch().accept(this);
		env = trueBranchEnv.getOuterScopeEnv();
		
		/*
		 * Lex the false branch
		 */
		LexerEnv falseBranchEnv = new LexerEnv(env);
		env = falseBranchEnv;
		selectNode.getNodeInFalseBranch().accept(this);
		env = falseBranchEnv.getOuterScopeEnv();
		
		/*
		 * Combine results
		 */
		env.updateAfterLexingBranches(selectNode.getConstraint(), trueBranchEnv, falseBranchEnv);
		
		return false;
	}
	
	/**
	 * Lex a RepeatNode
	 */
	@Override
	public boolean visitRepeatNode(RepeatNode repeatNode) {
		// TODO Should we consider RepeatNode as SelectNode or LiteralNode 
		return super.visitRepeatNode(repeatNode);
	}

	/**
	 * Lex a LiteralNode
	 */
	@Override
	public boolean visitLiteralNode(LiteralNode literalNode) {
		String htmlCode = literalNode.getStringValue();
		PositionRange htmlLocation = literalNode.getLocation();
		
		lexer.lex(htmlCode, htmlLocation, env);
		
		return true;
   	}
	
}

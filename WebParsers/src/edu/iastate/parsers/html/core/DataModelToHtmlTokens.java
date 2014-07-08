package edu.iastate.parsers.html.core;

import java.util.ArrayList;

import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.HtmlLexer;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.tree.TreeNode;
import edu.iastate.parsers.tree.TreeNodeFactory;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode.UnsetNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelToHtmlTokens {
	
	private TreeNode<HtmlToken> lexResult = null;
	
	private void updateLexResult(HtmlLexer lexer) {
		ArrayList<HtmlToken> currentResult = lexer.getLexResult();
		TreeNode<HtmlToken> curResult = new TreeNodeFactory<HtmlToken>().createInstanceFromNodes(currentResult);
		lexResult = new TreeNodeFactory<HtmlToken>().createCompactConcatNode(lexResult, curResult);
	}
	
	public TreeNode<HtmlToken> lex(DataModel dataModel) {
		// Lex the dataModel and update the lexResult along the way
		HtmlLexer lexer = new HtmlLexer();
		lex(dataModel.getRoot(), lexer);

		// Get the remaining result
		updateLexResult(lexer);
		lexer.clearLexResult();
		
		return lexResult;
	}
	
	public void lex(DataNode dataNode, HtmlLexer lexer) {
		if (dataNode instanceof ConcatNode)
			lex((ConcatNode) dataNode, lexer);
		
		else if (dataNode instanceof SelectNode)
			lex((SelectNode) dataNode, lexer);
		
		else if (dataNode instanceof RepeatNode)
			lex((RepeatNode) dataNode, lexer);
		
		else if (dataNode instanceof SymbolicNode)
			lex((SymbolicNode) dataNode, lexer);
		
		else if (dataNode instanceof LiteralNode)
			lex((LiteralNode) dataNode, lexer);
		
		else if (dataNode instanceof UnsetNode)
			{/* Do nothing */}
		
		else // Consider as SymbolicNode
			lex(DataNodeFactory.createSymbolicNode(), lexer);
	}
	
	/**
	 * Lex a ConcatNode
	 */
	private void lex(ConcatNode concatNode, HtmlLexer lexer) {
		for (DataNode childNode : concatNode.getChildNodes())
			lex(childNode, lexer);
	}
	
	/**
	 * Lex a RepeatNode
	 */
	private void lex(RepeatNode repeatNode, HtmlLexer lexer) {
		lex(repeatNode.getChildNode(), lexer);
	}
	
	/**
	 * Lex a SymbolicNode
	 */
	private void lex(SymbolicNode symbolicNode, HtmlLexer lexer) {
		// Use '1' to replace the symbolic value.
		LiteralNode literalNode = DataNodeFactory.createLiteralNode(symbolicNode.getPositionRange(), "1");
		lex(literalNode, lexer);
	}
	
	/**
	 * Lex a SelectNode
	 */
	private void lex(SelectNode selectNode, HtmlLexer lexer) {
		// Get result before entering the branches
		updateLexResult(lexer);
		
		/*
		 *  Enter the true branch
		 */
		lexer.clearLexResult();
		int savedLexicalState = lexer.saveLexicalState();
		
		lex(selectNode.getNodeInTrueBranch(), lexer);
		
		ArrayList<HtmlToken> tokensInTrueBranch = lexer.getLexResult();
		
		/*
		 * Enter the false branch
		 */
		lexer.clearLexResult();
		lexer.restoreLexicalState(savedLexicalState);
		
		lex(selectNode.getNodeInFalseBranch(), lexer);
		
		ArrayList<HtmlToken> tokensInFalseBranch = lexer.getLexResult();
		
		/*
		 * Combine results and continue
		 */
		TreeNode<HtmlToken> mergedResult =  new TreeNodeFactory<HtmlToken>().createInstanceFromBranchingNodes(selectNode.getConstraint(), tokensInTrueBranch, tokensInFalseBranch);
		lexResult = new TreeNodeFactory<HtmlToken>().createCompactConcatNode(lexResult, mergedResult);
		lexer.clearLexResult();
	}

	/**
	 * Lex a LiteralNode
	 */
	private void lex(LiteralNode literalNode, HtmlLexer lexer) {
		String htmlCode = literalNode.getStringValue();
		PositionRange htmlLocation = literalNode.getLocation();
		
		lexer.lex(htmlCode, htmlLocation);
   	}
	
}

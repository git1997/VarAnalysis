package edu.iastate.parsers.html.core;

import java.util.ArrayList;

import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListFactory;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.htmlparser.HtmlLexer;
import edu.iastate.parsers.html.htmlparser.HtmlLexer.LexicalState;
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
	
	private CondListFactory<HtmlToken> condListFactory = new CondListFactory<HtmlToken>();
	
	/**
	 * Lex a DataModel and return a conditional list of tokens
	 */
	public CondList<HtmlToken> lex(DataModel dataModel) {
		HtmlLexer lexer = new HtmlLexer();
		return lex(dataModel.getRoot(), lexer);
	}
	
	/**
	 * Lex a general DataNode
	 */
	private CondList<HtmlToken> lex(DataNode dataNode, HtmlLexer lexer) {
		if (dataNode instanceof ConcatNode)
			return lex((ConcatNode) dataNode, lexer);
		
		else if (dataNode instanceof SelectNode)
			return lex((SelectNode) dataNode, lexer);
		
		else if (dataNode instanceof RepeatNode)
			return lex((RepeatNode) dataNode, lexer);
		
		else if (dataNode instanceof SymbolicNode)
			return lex((SymbolicNode) dataNode, lexer);
		
		else if (dataNode instanceof LiteralNode)
			return lex((LiteralNode) dataNode, lexer);
		
		else if (dataNode instanceof UnsetNode)
			return null;
		
		else // Consider other nodes as SymbolicNode
			return lex(DataNodeFactory.createSymbolicNode(), lexer);
	}
	
	/**
	 * Lex a ConcatNode
	 */
	private CondList<HtmlToken> lex(ConcatNode concatNode, HtmlLexer lexer) {
		ArrayList<CondList<HtmlToken>> lexResult = new ArrayList<CondList<HtmlToken>>();
		for (DataNode childNode : concatNode.getChildNodes())
			lexResult.add(lex(childNode, lexer));
		return condListFactory.createCompactConcat(lexResult);
	}
	
	/**
	 * Lex a RepeatNode
	 */
	private CondList<HtmlToken> lex(RepeatNode repeatNode, HtmlLexer lexer) {
		return lex(repeatNode.getChildNode(), lexer);
	}
	
	/**
	 * Lex a SymbolicNode
	 */
	private CondList<HtmlToken> lex(SymbolicNode symbolicNode, HtmlLexer lexer) {
		// Use '111..' to replace the symbolic value.
		// Make its length equal to the length of the symbolic value's location to support location tracing.
		int length = symbolicNode.getLocation().getLength();
		String symbolicValue = "";
		for (int i = 0; i < length; i++)
			symbolicValue = symbolicValue + "1";
		
		LiteralNode literalNode = DataNodeFactory.createLiteralNode(symbolicValue, symbolicNode.getLocation());
		return lex(literalNode, lexer);
	}
	
	/**
	 * Lex a SelectNode
	 */
	private CondList<HtmlToken> lex(SelectNode selectNode, HtmlLexer lexer) {
		/*
		 * Enter the true branch
		 */
		LexicalState savedLexicalState = lexer.saveLexicalState();
		CondList<HtmlToken> tokensInTrueBranch = lex(selectNode.getNodeInTrueBranch(), lexer);
		
		/*
		 * Enter the false branch
		 */
		lexer.restoreLexicalState(savedLexicalState);
		CondList<HtmlToken> tokensInFalseBranch = lex(selectNode.getNodeInFalseBranch(), lexer);
		
		/*
		 * Combine results
		 */
		CondList<HtmlToken> mergedResult = condListFactory.createCompactSelect(selectNode.getConstraint(), tokensInTrueBranch, tokensInFalseBranch);
		return mergedResult;
	}

	/**
	 * Lex a LiteralNode
	 */
	private CondList<HtmlToken> lex(LiteralNode literalNode, HtmlLexer lexer) {
		String htmlCode = literalNode.getStringValue();
		PositionRange htmlLocation = literalNode.getLocation();
		
		lexer.lex(htmlCode, htmlLocation);
		
		CondList<HtmlToken> lexResult = condListFactory.createCondList(lexer.getLexResult());
		lexer.clearLexResult();
		
		return lexResult;
   	}
	
}

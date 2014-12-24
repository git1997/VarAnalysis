package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class DomParserEnv {
	
	/*
	 * The outerScopeEnv
	 */
	private DomParserEnv outerScopeEnv;
	
	/*
	 * State of the parser
	 */
	private HashSet<HtmlElement> currentHtmlElements; // Parsing state is a set of HtmlElements that are currently being modified
	
	/*
	 * The parse result
	 * (A map from HtmlElement to its added childNodes)
	 */
	private HashMap<HtmlElement, ArrayList<HtmlNode>> elementMap = new HashMap<HtmlElement, ArrayList<HtmlNode>>();
	
	// This set contains HtmlElements that are created in the current scope.
	private HashSet<HtmlElement> createdElements = new HashSet<HtmlElement>();
	
	/**
	 * Constructor
	 */
	public DomParserEnv() {
		this.outerScopeEnv = null;
		this.currentHtmlElements = new HashSet<HtmlElement>();
		
		// Create a pseudo HtmlElement to represent the root element
		HtmlElement pseudoRoot = HtmlElement.createHtmlElement(new HOpenTag("PSEUDO_ROOT", PositionRange.UNDEFINED));
		this.currentHtmlElements.add(pseudoRoot);
	}
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public DomParserEnv(DomParserEnv outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		this.currentHtmlElements = new HashSet<HtmlElement>(outerScopeEnv.currentHtmlElements);
	}
	
	/**
	 * Returns the outerScopeEnv
	 */
	public DomParserEnv getOuterScopeEnv() {
		return outerScopeEnv;
	}
	
	/*
	 * Protected methods, called by HtmlDomParser only.
	 */
	
	protected void changeStateToHtmlElement(HtmlElement childElement) {
		this.currentHtmlElement = childElement;
	}
	
	protected void changeStateToParentElement() {
		this.currentHtmlElement = currentHtmlElement.getParentElement();
	}
	
	protected HtmlElement getCurrentHtmlElement() {
		return currentHtmlElement;
	}
	
	protected boolean closeTagIsValid(HCloseTag closeTag) {
		return closeTag.getType().equals(currentHtmlElement.getType());
	}
	
	protected void addHtmlNodeToCurrentHtmlElement(HtmlNode htmlNode) {
		addHtmlNodeToHtmlElement(htmlNode, currentHtmlElement);
	}
	
	private void addHtmlNodeToHtmlElement(HtmlNode htmlNode, HtmlElement htmlElement) {
		htmlElement.addChildNode(htmlNode);
		
		if (!elementMap.containsKey(htmlElement))
			elementMap.put(htmlElement, new ArrayList<HtmlNode>());
		elementMap.get(htmlElement).add(htmlNode);
	}
	
	protected void addCloseTagToCurrentHtmlElement(HCloseTag closeTag) {
		// Could do the same as elementMap if we want to track the constraints of the closeTags.
		// However currently each HtmlElement contains a set of closeTags (without constraints), 
		//	 therefore we can freely add closeTags in the branches without worrying about backtracking.
		currentHtmlElement.addCloseTag(closeTag);
	}
	
	/**
	 * Updates the current Env after parsing two branches
	 */
	public void updateAfterParsingBranches(Constraint constraint, DomParserEnv trueBranchEnv, DomParserEnv falseBranchEnv) {
		/*
		 * Combine parseResults in the two branches
		 */
		HashSet<HtmlElement> elementMapKeySet = new HashSet<HtmlElement>();
		elementMapKeySet.addAll(trueBranchEnv.elementMap.keySet());
		elementMapKeySet.addAll(falseBranchEnv.elementMap.keySet());
		
		for (HtmlElement htmlElement : elementMapKeySet) {
			ArrayList<HtmlNode> childNodesInTrueBranch = (trueBranchEnv.elementMap.containsKey(htmlElement) ? trueBranchEnv.elementMap.get(htmlElement) : new ArrayList<HtmlNode>());
			ArrayList<HtmlNode> childNodesInFalseBranch = (falseBranchEnv.elementMap.containsKey(htmlElement) ? falseBranchEnv.elementMap.get(htmlElement) : new ArrayList<HtmlNode>());
			
			HtmlNode resultInTrueBranch = HtmlConcat.createCompactConcat(childNodesInTrueBranch);
			HtmlNode resultInFalseBranch = HtmlConcat.createCompactConcat(childNodesInFalseBranch);
			HtmlNode select = HtmlSelect.createCompactSelect(constraint, resultInTrueBranch, resultInFalseBranch);
			
			htmlElement.removeLastChildNodes(childNodesInTrueBranch.size() + childNodesInFalseBranch.size());
			this.addHtmlNodeToHtmlElement(select, htmlElement);
		}
		
		/*
		 * Check the state
		 */
		if (trueBranchEnv.getCurrentHtmlElement() != falseBranchEnv.getCurrentHtmlElement()) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DomParserEnv.java: DomParser ends up in different states after branches: trueBranchState=" + trueBranchEnv.getCurrentHtmlElement().getOpenTag().toDebugString()  + " vs. falseBranchState=" + falseBranchEnv.getCurrentHtmlElement().getOpenTag().toDebugString());
		}
		
		// Use the state of the false branch since it's more likely to be a normal state
		changeStateToHtmlElement(falseBranchEnv.getCurrentHtmlElement());
	}
	
}

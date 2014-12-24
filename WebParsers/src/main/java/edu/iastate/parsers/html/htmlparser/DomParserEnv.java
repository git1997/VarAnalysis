package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlEmpty;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
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
	 * (A stack of HtmlElements. Each layer in the stack contains HTML elements that are currently being modified.
	 * For two consecutive layers, HtmlElements in the lower layer are parents of those in the higher layer.
	 * HtmlElements in the same layer must have the same name.) 
	 */
	private Stack<String> htmlStack;
	private HashSet<HtmlElement> currentHtmlElements;
	
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
		this.htmlStack = new Stack<String>();
		this.currentHtmlElements = new HashSet<HtmlElement>();
		
		// Create a pseudo HtmlElement to represent the root element
		HtmlElement pseudoRoot = HtmlElement.createHtmlElement(new HOpenTag("PSEUDO_ROOT", PositionRange.UNDEFINED));
		this.htmlStack.push(pseudoRoot.getType());
		this.currentHtmlElements = new HashSet<HtmlElement>();
		this.currentHtmlElements.add(pseudoRoot);
	}
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public DomParserEnv(DomParserEnv outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		this.htmlStack = outerScopeEnv.htmlStack;
		this.currentHtmlElements = new HashSet<HtmlElement>(outerScopeEnv.currentHtmlElements);
	}
	
	/**
	 * Returns the outerScopeEnv
	 */
	public DomParserEnv getOuterScopeEnv() {
		return outerScopeEnv;
	}
	
	/**
	 * Returns the parseResult
	 */
	public HtmlDocument getParseResult() {
		HtmlNode node = currentHtmlElements.iterator().next();
		while (!node.getParentNodes().isEmpty())
			node = node.getParentNodes().iterator().next();
		// node should be the pseudoRoot now.
		
		return new HtmlDocument(node.getChildNodes());
	}
	
	/*
	 * Protected methods, called by HtmlDomParser only.
	 */
	
	protected String getCurrentHtmlElementType() {
		return htmlStack.peek();
	}
	
	protected boolean closeTagIsValid(HCloseTag closeTag) {
		return closeTag.getType().equals(getCurrentHtmlElementType());
	}
	
	protected void pushHtmlStack(HtmlElement htmlElement) {
		for (HtmlElement element : currentHtmlElements) {
			element.addChildNode(htmlElement);
			recordModifications(element, htmlElement);
		}
		
		recordCreatedElement(htmlElement);
		
		htmlStack.push(htmlElement.getType());
		currentHtmlElements = new HashSet<HtmlElement>();
		currentHtmlElements.add(htmlElement);
	}
	
	protected void recordCreatedElement(HtmlElement htmlElement) {
		createdElements.add(htmlElement);
	}
	
	/**
	 * Record modifications in the currentEnv
	 */
	private void recordModifications(HtmlElement parent, HtmlNode child) {
		if (createdElements.contains(parent))
			return; // Don't need to record those created in the currentEnv
		
		if (!elementMap.containsKey(parent))
			elementMap.put(parent, new ArrayList<HtmlNode>());
		elementMap.get(parent).add(child);
	}
	
	protected void addHtmlText(HtmlText htmlText) {
		for (HtmlElement element : currentHtmlElements) {
			element.addChildNode(htmlText);
			recordModifications(element, htmlText);
		}
	}
	
	protected void popHtmlStack() {
		htmlStack.pop();
		HashSet<HtmlElement> parentSet = new HashSet<HtmlElement>();
		for (HtmlElement element : currentHtmlElements) {
			parentSet.addAll(getParentElements(element));
		}
		currentHtmlElements = parentSet;
	}
		
	private HashSet<HtmlElement> getParentElements(HtmlNode htmlNode) {
		HashSet<HtmlElement> set = new HashSet<HtmlElement>();
		for (HtmlNode node : htmlNode.getParentNodes()) {
			if (node instanceof HtmlElement)
				set.add((HtmlElement) node);
			else {
				for (HtmlNode node2 : node.getParentNodes())
					set.addAll(getParentElements(node2));
			}
		}
		return set;
	}
		
	protected void addCloseTagToCurrentHtmlElement(HCloseTag closeTag) {
		// Could do the same as elementMap if we want to track the constraints of the closeTags.
		// However currently each HtmlElement contains a set of closeTags (without constraints), 
		//	 therefore we can freely add closeTags in the branches without worrying about backtracking.
		for (HtmlElement element : currentHtmlElements)
			element.addCloseTag(closeTag);
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
			
			if (!(select instanceof HtmlEmpty)) {
				htmlElement.addChildNode(select);
				recordModifications(htmlElement, select);
			}
		}
		
		/*
		 * Check the state
		 */
		String stackInTrueBranch = stackToString(trueBranchEnv.htmlStack);
		String stackInFalseBranch = stackToString(falseBranchEnv.htmlStack);
		if (!stackInTrueBranch.equals(stackInFalseBranch)) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In DomParserEnv.java: DomParser ends up in different states after branches: trueBranchState=" + stackInTrueBranch + " vs. falseBranchState=" + stackInFalseBranch);
		}
		
		// Find the common stack between the two stacks
		htmlStack = findCommonStack(trueBranchEnv.htmlStack, falseBranchEnv.htmlStack);
		
		// Backtrack to the common stack
		while (trueBranchEnv.htmlStack.size() > htmlStack.size())
			trueBranchEnv.popHtmlStack();
		while (falseBranchEnv.htmlStack.size() > htmlStack.size())
			falseBranchEnv.popHtmlStack();
		
		currentHtmlElements = new HashSet<HtmlElement>();
		currentHtmlElements.addAll(trueBranchEnv.currentHtmlElements);
		currentHtmlElements.addAll(falseBranchEnv.currentHtmlElements);
	}
	
	private String stackToString(Stack<String> htmlStack) {
		StringBuilder str = new StringBuilder();
		for (String element : htmlStack)
			str.append("<" + element + ">");
		return str.toString();
	}
	
	private Stack<String> findCommonStack(Stack<String> htmlStack1, Stack<String> htmlStack2) {
		Stack<String> stack = new Stack<String>();
		for (int i = 0; i < Math.min(htmlStack1.size(), htmlStack2.size()); i++) {
			String element1 = htmlStack1.get(i);
			String element2 = htmlStack2.get(i);
			if (element1.equals(element2))
				stack.push(element1);
			else
				break;
		}
		return stack;
	}
	
}

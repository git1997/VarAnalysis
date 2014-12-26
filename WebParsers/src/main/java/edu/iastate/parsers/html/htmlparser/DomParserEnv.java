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
	 * State of the parser.
	 * 	- htmlStack is a stack of HtmlElements' types. Each layer in the stack is the type of the HTML elements 
	 * that are currently being modified. For two consecutive layers, HtmlElements in the lower layer are parents
	 * of those in the higher layer.
	 *  - currentHtmlElements is the set of HtmlElements that are being modified in the current (top) layer.
	 * (There could be multiple HtmlElements being modified at the same time because they are alternatives
	 * depending on a condition, e.g., two open tags with the same close tag.)
	 * To enforce well-formedness, we require that HtmlElements in the same layer must have the same type.
	 */
	private Stack<String> htmlStack;
	private HashSet<HtmlElement> currentHtmlElements;
	
	/*
	 * The parse result
	 * A map from HtmlElement (in an outerScopeEnv) to its added childNodes in the current scope
	 * 	 (i.e., the effects the current scope has on outerScopeEnvs).
	 * Note: Modifications to HTML elements created in the current scope need not be reported in this parse result,
	 * 	 since one of their parents or ancestors must have been included in the parse result already.
	 * For example, if outerScopeEnv.currentHtmlElements contains <a>, <b>, and currentEnv adds <c> as
	 *	 child node of <a>, <b>, and <d> as child node of <c>, then we report the added child node
	 *	 at <a> and <b>, and don't have to report the added child node at <c>.
	 */
	private HashMap<HtmlElement, ArrayList<HtmlNode>> elementMap = new HashMap<HtmlElement, ArrayList<HtmlNode>>();
	
	// This set contains HtmlElements that are created in the current scope (see note above)
	private HashSet<HtmlElement> elementsCreatedInCurrentScope = new HashSet<HtmlElement>();
	
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
		this.currentHtmlElements.add(pseudoRoot);
	}
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public DomParserEnv(DomParserEnv outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		this.htmlStack = new Stack<String>();
		for (String element : outerScopeEnv.htmlStack)
			this.htmlStack.push(element);
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
		// node should be the pseudoRoot now (see DomParserEnv.DomParserEnv())
		
		return new HtmlDocument(node.getChildNodes());
	}
	
	/*
	 * Protected methods, called by HtmlDomParser only.
	 */
	
	protected String getCurrentHtmlElementType() {
		return htmlStack.peek();
	}
	
	protected boolean closeTagMatchedWithOpenTag(HCloseTag closeTag) {
		return closeTag.getType().equals(getCurrentHtmlElementType());
	}
	
	protected HtmlElement createHtmlElementFromOpenTag(HOpenTag openTag) {
		HtmlElement htmlElement = HtmlElement.createHtmlElement(openTag);
		elementsCreatedInCurrentScope.add(htmlElement);
		return htmlElement;
	}
	
	protected void pushHtmlStack(HtmlElement htmlElement) {
		for (HtmlElement parent : currentHtmlElements) {
			parent.addChildNode(htmlElement);
			recordModifications(parent, htmlElement);
		}
		
		htmlStack.push(htmlElement.getType());
		currentHtmlElements = new HashSet<HtmlElement>();
		currentHtmlElements.add(htmlElement);
	}
	
	protected void addHtmlTextToCurrentHtmlElement(HtmlText htmlText) {
		for (HtmlElement parent : currentHtmlElements) {
			parent.addChildNode(htmlText);
			recordModifications(parent, htmlText);
		}
	}
	
	protected void addCloseTagToCurrentHtmlElement(HCloseTag closeTag) {
		// If we want to track the constraints of the closeTags, we could record this type of modifications
		// similarly to the case where a new child node is added the the current HtmlElement
		// (@see DomParserEnv.recordModifications(HtmlElement, HtmlNode)).
		// However, currently each HtmlElement contains a set of closeTags (without constraints), 
		//	 therefore we can freely add closeTags in the branches without worrying about combining them after branches.
		for (HtmlElement element : currentHtmlElements)
			element.addCloseTag(closeTag);
	}
	
	/**
	 * Record modifications made to HtmlElements from an outerScopeEnv
	 */
	private void recordModifications(HtmlElement parent, HtmlNode child) {
		if (elementsCreatedInCurrentScope.contains(parent))
			return; // Don't need to record modifications to HtmlElements created in the currentEnv
		
		if (!elementMap.containsKey(parent))
			elementMap.put(parent, new ArrayList<HtmlNode>());
		elementMap.get(parent).add(child);
	}
	
	protected void popHtmlStack() {
		htmlStack.pop();
		HashSet<HtmlElement> parentElements = new HashSet<HtmlElement>();
		for (HtmlElement element : currentHtmlElements) {
			parentElements.addAll(getParentElements(element));
		}
		currentHtmlElements = parentElements;
	}
		
	private HashSet<HtmlElement> getParentElements(HtmlNode htmlNode) {
		HashSet<HtmlElement> parentElements = new HashSet<HtmlElement>();
		for (HtmlNode node : htmlNode.getParentNodes()) {
			if (node instanceof HtmlElement)
				parentElements.add((HtmlElement) node);
			else // node is either HtmlSelect or HtmlConcat (HtmlText and HtmlEmpty can't be a parent node)
				parentElements.addAll(getParentElements(node));
		}
		return parentElements;
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
			
			// Backtrack from the two branches
			htmlElement.removeLastChildNodes(childNodesInTrueBranch.size() + childNodesInFalseBranch.size());
						
			// Combine results from the two branches
			HtmlNode resultInTrueBranch = HtmlConcat.createCompactConcat(childNodesInTrueBranch);
			HtmlNode resultInFalseBranch = HtmlConcat.createCompactConcat(childNodesInFalseBranch);
			HtmlNode select = HtmlSelect.createCompactSelect(constraint, resultInTrueBranch, resultInFalseBranch);
			
			// Update the htmlElement with the combined results
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
		
		// Update the htmlStack with the common stack between the two stacks
		// (Note that the updated stack could be longer or shorter than the original stack)
		htmlStack = findCommonStack(trueBranchEnv.htmlStack, falseBranchEnv.htmlStack);
		
		// Backtrack the branches to the common stack
		while (trueBranchEnv.htmlStack.size() > htmlStack.size())
			trueBranchEnv.popHtmlStack();
		while (falseBranchEnv.htmlStack.size() > htmlStack.size())
			falseBranchEnv.popHtmlStack();
		
		// Update currentHtmlElements with the currentHtmlElements in the branches at the top of the common stack
		// (Even though the stack is common between the two branches, the sets of currentHtmlElements in the branches
		// can still be different because the htmlStack contains elements' types only, not the actual elements.)
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
	
	/**
	 * Returns the common stack that is the prefix of the two stacks.
	 */
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

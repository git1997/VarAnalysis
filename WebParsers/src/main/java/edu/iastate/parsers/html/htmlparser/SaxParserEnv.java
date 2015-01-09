package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;
import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.conditional.CondListEmpty;
import edu.iastate.parsers.conditional.CondListFactory;
import edu.iastate.parsers.conditional.CondListItem;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class SaxParserEnv {
	
	private static CondListFactory<HtmlSaxNode> condListFactory = new CondListFactory<HtmlSaxNode>();

	/*
	 * The outerScopeEnv
	 */
	private SaxParserEnv outerScopeEnv;
	
	/*
	 * State of the parser
	 */
	public enum ParsingState { 
		OUTSIDE_TEXT, 		// Finished reading an HOpenTag or HCloseTag 
		INSIDE_OPEN_TAG, 	// Is reading an HOpenTag
		INSIDE_TEXT 		// Finished reading an HText 
	}
	
	private ParsingState parsingState;
	
	/*
	 * The parse result
	 */
	private ArrayList<CondList<HtmlSaxNode>> parseResult = new ArrayList<CondList<HtmlSaxNode>>();
	
	/*
	 * Sometimes, branching takes place when the parser is inside an openTag.
	 * In that case, the Env in the branch "borrows" the openTag from the outerEnv.
	 */
	private HOpenTag borrowedOpenTag = null;
	
	/**
	 * Constructor
	 */
	public SaxParserEnv() {
		this.outerScopeEnv = null;
		this.parsingState = ParsingState.OUTSIDE_TEXT;
	}
	
	/**
	 * Constructor
	 * @param outerScopeEnv
	 */
	public SaxParserEnv(SaxParserEnv outerScopeEnv) {
		this.outerScopeEnv = outerScopeEnv;
		this.parsingState = outerScopeEnv.parsingState;
	}
	
	/**
	 * Returns the outerScopeEnv
	 */
	public SaxParserEnv getOuterScopeEnv() {
		return outerScopeEnv;
	}
	
	/**
	 * Returns the parseResult
	 */
	public CondList<HtmlSaxNode> getParseResult() {
		return condListFactory.createCompactConcat(new ArrayList<CondList<HtmlSaxNode>>(parseResult));
	}
	
	/*
	 * Protected methods, called by HtmlSaxParser only.
	 */
	
	protected void setParsingState(ParsingState parsingState) {
		this.parsingState = parsingState;
	}
	
	protected ParsingState getParsingState() {
		return parsingState;
	}
	
	protected boolean isInsideOpenTag() {
		return parsingState == ParsingState.INSIDE_OPEN_TAG;
	}
	
	protected boolean isInsideText() {
		return parsingState == ParsingState.INSIDE_TEXT;
	}
	
	protected void addHtmlSaxNode(HtmlSaxNode htmlSaxNode) {
		parseResult.add(condListFactory.createCondListItem(htmlSaxNode));
	}
	
	/**
	 * Returns an OpenTag if the last parsed SAX node is an OpenTag.
	 * Also, handle the case where branching takes place inside an OpenTag (the current parseResult in the branch is empty).
	 */
	protected HOpenTag tryGetLastOpenTag() {
		if (!parseResult.isEmpty()) {
			CondList<HtmlSaxNode> lastElement = parseResult.get(parseResult.size() - 1);
			if (lastElement instanceof CondListItem<?>) {
				HtmlSaxNode lastSaxNode = ((CondListItem<HtmlSaxNode>) lastElement).getItem();
				if (lastSaxNode instanceof HOpenTag)
					return (HOpenTag) lastSaxNode;
				else {
					MyLogger.log(MyLevel.USER_EXCEPTION, "In SaxParserEnv.java: Expected HOpenTag but found " + lastSaxNode.getClass().getSimpleName());
					return null;
				}
			}
			else {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In SaxParserEnv.java: Expected HOpenTag but found " + lastElement.getClass().getSimpleName());
				return null;
			}
		}
		else {
			if (borrowedOpenTag != null)
				return borrowedOpenTag;
			
			if (outerScopeEnv != null) {
				// Borrow the openTag from the outerScopeEnv
				HOpenTag openTag = outerScopeEnv.tryGetLastOpenTag();
				if (openTag != null) {
					borrowedOpenTag = new HOpenTag(openTag.getType(), openTag.getLocation()); // Create a new OpenTag (with 0 attributes) so the original OpenTag is untouched
					
					// Also, borrow the last attribute of that openTag (if any), because the parser may need to update the last attribute of the last OpenTag
					HtmlAttribute attribute = openTag.getLastAttributeOrNull();
					if (attribute != null)
						borrowedOpenTag.addAttribute(attribute.cloneWithoutConstraint()); // Get a clone copy so the original attribute is untouched
					
					return borrowedOpenTag;
				}
				else
					return null;
			}
			else {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In SaxParserEnv.java: Expected HOpenTag but couldn't find it.");
				return null;
			}
		}
	}
	
	/**
	 * Updates the current Env after parsing two branches
	 */
	public void updateAfterParsingBranches(Constraint constraint, SaxParserEnv trueBranchEnv, SaxParserEnv falseBranchEnv) {
		/*
		 * Check the state
		 */
		if (trueBranchEnv.isInsideOpenTag() != falseBranchEnv.isInsideOpenTag()) {
			// If both branches are outside an open tag, it doesn't matter if one is OUTSIDE_TEXT and the other is INSIDE_TEXT.
			// E.g., <div>#if ($C) 'abc' #else '' #endif, then the state after the true branch is INSIDE_TEXT and the state after the false branch is OUTSIDE_TEXT.
			// This is not an error. The distinction between these two states only serves to eliminate empty texts between open tags 
			// (see HtmlSaxParser.parse(HtmlToken, SaxParserEnv). 
			MyLogger.log(MyLevel.USER_EXCEPTION, "In SaxParserEnv.java: SaxParser ends up in different states after branches: " +
														"before=" + this.parsingState + "; true=" + trueBranchEnv.parsingState + " vs. false=" + falseBranchEnv.parsingState + 
														" | Last SaxNode: " + 
														"before=" + getLastSaxNodeInParseResult(this.parseResult) + "; true=" + getLastSaxNodeInParseResult(trueBranchEnv.parseResult) + " vs. false=" + getLastSaxNodeInParseResult(falseBranchEnv.parseResult));
		}
		
		/*
		 * If branching takes place when the parser is inside an openTag, then update the attributes of this openTag.
		 */
		if (trueBranchEnv.borrowedOpenTag != null || falseBranchEnv.borrowedOpenTag != null) {
			// Get the attributes added in the branches, the first of them may be cloned from the outerEnv (which is this Env). @see SaxParserEnv.tryGetLastOpenTag()
			ArrayList<HtmlAttribute> attrsInTrueBranch = (trueBranchEnv.borrowedOpenTag != null ? trueBranchEnv.borrowedOpenTag.getAttributes() : new ArrayList<HtmlAttribute>());
			ArrayList<HtmlAttribute> attrsInFalseBranch = (falseBranchEnv.borrowedOpenTag != null ? falseBranchEnv.borrowedOpenTag.getAttributes() : new ArrayList<HtmlAttribute>());
			
			// Get the endBrackets added in the branches
			ArrayList<HtmlToken> endBracketsInTrueBranch = (trueBranchEnv.borrowedOpenTag != null ? trueBranchEnv.borrowedOpenTag.getEndBrackets() : new ArrayList<HtmlToken>());
			ArrayList<HtmlToken> endBracketsInFalseBranch = (falseBranchEnv.borrowedOpenTag != null ? falseBranchEnv.borrowedOpenTag.getEndBrackets() : new ArrayList<HtmlToken>());
			
			// Get the openTag before branching and its last attribute
			HOpenTag openTag = this.tryGetLastOpenTag(); // not null
			HtmlAttribute lastAttribute = openTag.getLastAttributeOrNull(); // can be null
			
			// Update the last attribute of the openTag if it is modified in the branches
			if (lastAttribute != null) {
				HtmlAttribute borrowedAttrInTrueBranch = (trueBranchEnv.borrowedOpenTag != null ? attrsInTrueBranch.remove(0) : lastAttribute.cloneWithoutConstraint());
				HtmlAttribute borrowedAttrInFalseBranch = (falseBranchEnv.borrowedOpenTag != null ? attrsInFalseBranch.remove(0) : lastAttribute.cloneWithoutConstraint());

				Constraint notConstraint = ConstraintFactory.createNotConstraint(constraint);
				Constraint trueConstraint = ConstraintFactory.createAndConstraint(ConstraintFactory.createAndConstraint(lastAttribute.getConstraint(), constraint), borrowedAttrInTrueBranch.getConstraint());
				Constraint falseConstraint = ConstraintFactory.createAndConstraint(ConstraintFactory.createAndConstraint(lastAttribute.getConstraint(), notConstraint), borrowedAttrInFalseBranch.getConstraint());
				
				borrowedAttrInTrueBranch.setConstraint(trueConstraint);
				borrowedAttrInFalseBranch.setConstraint(falseConstraint);
				
				// NOTE: In difficult cases, the constraint of an attribute may be incomplete, and some values may be missing.
				// For xample: If branching takes place at attribute A (name=n, value=v) under constraint C,
				//   and v is added with attribute values v1 and v2 in the branches, 
				// 	 then we replace A with two attributes A1 (name=n, value=v+v1, constraint=C) and A2 (name=n, value=v+v2, constraint=!C).
				// If branching takes place again right after that with added attribute values v3 and v4 under constraint D,
				//   strictly speaking, we should have 4 new attributes with values (v+v1+v3, C&D), (v+v1+v4, C&!D), (v+v2+v3, !C&D), (v+v2+v4, !C&!D).
				// However, in the current algorithm, only the last attribute is replaced,
				//   therefore, in this example we will end up with 3 attributes (v+v1, C), (v+v2+v3, !C&D), (v+v2+v4, !C&!D).
				// It should probably work for now because we expect to see branching taking place not too often in an attribute value.
				openTag.removeLastAttribute();
				if (compareAttributes(borrowedAttrInTrueBranch, borrowedAttrInFalseBranch)) {
					HtmlAttribute attribute = borrowedAttrInTrueBranch;
					attribute.setConstraint(ConstraintFactory.createOrConstraint(trueConstraint, falseConstraint));
					openTag.addAttribute(attribute);
				}
				else {
					openTag.addAttribute(borrowedAttrInTrueBranch);
					openTag.addAttribute(borrowedAttrInFalseBranch);
				}
			}
			
			// Add new attributes to the current openTag
			for (HtmlAttribute attr : attrsInTrueBranch) {
				attr.setConstraint(ConstraintFactory.createAndConstraint(constraint, attr.getConstraint()));
				openTag.addAttribute(attr);
			}
			for (HtmlAttribute attr : attrsInFalseBranch) {
				Constraint notConstraint = ConstraintFactory.createNotConstraint(constraint);
				attr.setConstraint(ConstraintFactory.createAndConstraint(notConstraint, attr.getConstraint()));
				openTag.addAttribute(attr);
			}
		
			// Add new endBrackets to the current openTag
			for (HtmlToken endBracket : endBracketsInTrueBranch) {
				openTag.addEndBracket(endBracket); // Currently, we don't assign a constraint to endBrackets
			}
			for (HtmlToken endBracket : endBracketsInFalseBranch) {
				openTag.addEndBracket(endBracket); // Currently, we don't assign a constraint to endBrackets
			}
		}
		
		/*
		 * Combine parseResults in the two branches
		 */
		CondList<HtmlSaxNode> parseResultInTrueBranch = trueBranchEnv.getParseResult();
		CondList<HtmlSaxNode> parseResultInFalseBranch = falseBranchEnv.getParseResult();
		CondList<HtmlSaxNode> select = condListFactory.createCompactSelect(constraint, parseResultInTrueBranch, parseResultInFalseBranch);
		if (!(select instanceof CondListEmpty<?>)) {
			parseResult.add(select);
		}
		
		/*
		 * Update the state
		 * Use the state of the false branch since it's more likely to be a normal state
		 */
		setParsingState(falseBranchEnv.getParsingState());
		
		// Handle this special case when both branches are outside an open tag, and one of them is inside text (e.g., <div>#if ($C) 'abc' #else '' #endif)
		if (!falseBranchEnv.isInsideOpenTag() && trueBranchEnv.isInsideText()) {
			setParsingState(ParsingState.INSIDE_TEXT);
		}
	}
	
	/*
	 * Utility methods
	 */
	
	/**
	 * Returns a string describing the last HtmlSaxNode in a parseResult (for debugging only)
	 */
	private String getLastSaxNodeInParseResult(ArrayList<CondList<HtmlSaxNode>> parseResult) {
		if (parseResult.isEmpty())
			return "(empty)";
		
		CondList<HtmlSaxNode> lastItem  = parseResult.get(parseResult.size() - 1);
		if (lastItem instanceof CondListItem<?>) {
			HtmlSaxNode lastSaxNode = ((CondListItem<HtmlSaxNode>) lastItem).getItem();
			return lastSaxNode.toDebugString() + " @ " + lastSaxNode.getLocation().getStartPosition().toDebugString();
		}
		else
			return lastItem.getClass().getSimpleName();
	}
	
	/**
	 * Returns true if the two attributes are the same (their constraints may still be different)
	 */
	private boolean compareAttributes(HtmlAttribute attr1, HtmlAttribute attr2) {
		return attr1.getName().equals(attr2.getName())
				&& attr1.getLocation() == attr2.getLocation()
				&& attr1.getStringValue().equals(attr2.getStringValue())
				&& attr1.getEqToken() == attr2.getEqToken()
				&& attr1.getAttrValStart() == attr2.getAttrValStart()
				&& attr1.getAttrValEnd() == attr2.getAttrValEnd();
	}
	
}

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
import edu.iastate.symex.position.PositionRange;
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
	 * In that case, the following information is used to record updated properties of the last attribute of the openTag
	 * 	 and the new attributes that are added to that openTag in the branch. 
	 */
	private HOpenTag pseudoOpenTag = null;
	
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
	 * @return
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
			// Create a pseudoOpenTag to store new attributes that are added to the openTag that is outside the branch
			if (pseudoOpenTag == null) {
				pseudoOpenTag = new HOpenTag("PSEUDO_OPEN_TAG", PositionRange.UNDEFINED);

				// Also create a pseudoAttribute to store updated properties of the last attribute of that openTag
				HtmlAttribute pseudoAttribute = new HtmlAttribute("PSEUDO_ATTRIBUTE", PositionRange.UNDEFINED);
				pseudoOpenTag.addAttribute(pseudoAttribute);
			}
			return pseudoOpenTag;
		}
	}
	
	/**
	 * Updates the current Env after parsing two branches
	 */
	public void updateAfterParsingBranches(Constraint constraint, SaxParserEnv trueBranchEnv, SaxParserEnv falseBranchEnv) {
		/*
		 * If branching takes place when the parser is inside an openTag, then update the attributes of this openTag.
		 */
		// Get the attributes added in the branches
		ArrayList<HtmlAttribute> attrsInTrueBranch = (trueBranchEnv.pseudoOpenTag != null ? trueBranchEnv.pseudoOpenTag.getAttributes() : new ArrayList<HtmlAttribute>());
		ArrayList<HtmlAttribute> attrsInFalseBranch = (falseBranchEnv.pseudoOpenTag != null ? falseBranchEnv.pseudoOpenTag.getAttributes() : new ArrayList<HtmlAttribute>());
		
		// Get the endBrackets added in the branches
		ArrayList<HtmlToken> endBracketsInTrueBranch = (trueBranchEnv.pseudoOpenTag != null ? trueBranchEnv.pseudoOpenTag.getEndBrackets() : new ArrayList<HtmlToken>());
		ArrayList<HtmlToken> endBracketsInFalseBranch = (falseBranchEnv.pseudoOpenTag != null ? falseBranchEnv.pseudoOpenTag.getEndBrackets() : new ArrayList<HtmlToken>());
					
		// Get and remove the first pseudoAttribute in the attribute list, @see SaxParserEnv.tryGetLastOpenTag().
		HtmlAttribute pseudoAttrInTrueBranch = (!attrsInTrueBranch.isEmpty() ? attrsInTrueBranch.remove(0) : null);
		HtmlAttribute pseudoAttrInFalseBranch = (!attrsInFalseBranch.isEmpty() ? attrsInFalseBranch.remove(0) : null);
		
		// Update the last attribute of the current openTag if it is modified in the branches
		if (pseudoAttrInTrueBranch != null && pseudoAttributeIsUpdated(pseudoAttrInTrueBranch) 
					|| pseudoAttrInFalseBranch != null && pseudoAttributeIsUpdated(pseudoAttrInFalseBranch)) {
			HOpenTag openTag = this.tryGetLastOpenTag();
			if (openTag != null) {
				HtmlAttribute attribute = openTag.getLastAttributeOrNull();
				if (attribute != null) { 
					HtmlAttribute attrInTrueBranch = (pseudoAttrInTrueBranch != null ? combineAttributeInfo(attribute, pseudoAttrInTrueBranch) : attribute);
					HtmlAttribute attrInFalseBranch = (pseudoAttrInFalseBranch != null ? combineAttributeInfo(attribute, pseudoAttrInFalseBranch) : attribute);
				
					// attribute might already have some constraint as well, but we ignore it for now (see the note below).
					attrInTrueBranch.setConstraint(constraint);
					attrInFalseBranch.setConstraint(ConstraintFactory.createNotConstraint(constraint));

					// Note: If branching takes place at attribute A (name=n, value=v), and A.v is updated with attribute values A.v1 and A.v2 in the branches, 
					// 	 then we replace A with two attributes A1 (name=n, value=v+v1, constraint=C) and A2 (name=n, value=v+v2, constraint=!C).
					// However, if branching takes place again right after that with attribute values A.v3 and A.v4, stricly speaking, 
					// 	 we should have 4 new attributes with values (v+v1+v3), (v+v1+v4), (v+v2+v3), (v+v2+v4).
					// In the current algorithm, only the last attribute is replaced,
					// so in that example we will end up with 3 attributes (v+v1), (v+v2+v3), (v+v2+v4).
					// It should probably work for now because we expect to see braching taking place at most 1 time in an attribute value.
					openTag.removeLastAttribute();
					openTag.addAttribute(attrInTrueBranch);
					openTag.addAttribute(attrInFalseBranch);
				}
				else {
					MyLogger.log(MyLevel.USER_EXCEPTION, "In SaxParserEnv.java: Can't find last attribute of open tag.");
				}
			}
		}
			
		// Add new attributes to the current openTag if they are added in the branches
		if (!attrsInTrueBranch.isEmpty() || !attrsInFalseBranch.isEmpty()) {
			HOpenTag openTag = this.tryGetLastOpenTag();
			if (openTag != null) {
				for (HtmlAttribute attr : attrsInTrueBranch) {
					attr.setConstraint(constraint);
					openTag.addAttribute(attr);
				}
				for (HtmlAttribute attr : attrsInFalseBranch) {
					attr.setConstraint(ConstraintFactory.createNotConstraint(constraint));
					openTag.addAttribute(attr);
				}
			}
		}
		
		// Add new endBrackets to the current openTag if they are added in the branches
		if (!endBracketsInTrueBranch.isEmpty() || !endBracketsInFalseBranch.isEmpty()) {
			HOpenTag openTag = this.tryGetLastOpenTag();
			if (openTag != null) {
				for (HtmlToken endBracket : endBracketsInTrueBranch) {
					openTag.addEndBracket(endBracket); // Currently, we don't assign a constraint to endBrackets
				}
				for (HtmlToken endBracket : endBracketsInFalseBranch) {
					openTag.addEndBracket(endBracket); // Currently, we don't assign a constraint to endBrackets
				}
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
		 * Check the state
		 */
		if (trueBranchEnv.getParsingState() != falseBranchEnv.getParsingState()) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In SaxParserEnv.java: SaxParser ends up in different states after branches: trueBranchState=" + trueBranchEnv.getParsingState()  + " vs. falseBranchState=" + falseBranchEnv.getParsingState());
		}
		
		// Use the state of the false branch since it's more likely to be a normal state
		setParsingState(falseBranchEnv.getParsingState());
	}
	
	/**
	 * Returns true if the pseudoAttribute is updated in the branch.
	 */
	private boolean pseudoAttributeIsUpdated(HtmlAttribute attribute) {
		return (!attribute.getStringValue().isEmpty() || attribute.getEqToken() != null || attribute.getAttrValStart() != null || attribute.getAttrValEnd() != null);
	}
	
	/**
	 * Creates a new attribute based on information extracted from the two attributes
	 */
	private HtmlAttribute combineAttributeInfo(HtmlAttribute attr1, HtmlAttribute attr2) {
		// Use the name and location of attribute 1
		HtmlAttribute attribute = new HtmlAttribute(attr1.getName(), attr1.getLocation());
		
		attribute.addAttrValFrag(attr1.getStringValue(), attr1.getAttributeValue().getLocation());
		attribute.addAttrValFrag(attr2.getStringValue(), attr2.getAttributeValue().getLocation());
		
		if (attr1.getEqToken() != null)
			attribute.setEqToken(attr1.getEqToken());
		else
			attribute.setEqToken(attr2.getEqToken());
				
		if (attr1.getAttrValStart() != null)
			attribute.setAttrValStart(attr1.getAttrValStart());
		else
			attribute.setAttrValStart(attr2.getAttrValStart());
		
		if (attr1.getAttrValEnd() != null) 
			attribute.setAttrValEnd(attr1.getAttrValEnd());
		else
			attribute.setAttrValEnd(attr2.getAttrValEnd());
		
		return attribute;
	}
	
}

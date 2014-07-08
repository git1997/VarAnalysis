package edu.iastate.parsers.html.htmlparser;

import java.util.ArrayList;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.parsers.tree.TreeNode;
import edu.iastate.parsers.tree.TreeNodeFactory;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlSaxParser {
	
	private ArrayList<HtmlSaxNode> parseResult = new ArrayList<HtmlSaxNode>();
	
	private TreeNode<HtmlSaxNode> parseResultBeforeBranching = null;
	private ArrayList<HtmlSaxNode> parseResultInOtherBranch = null;
	
	public ArrayList<HtmlSaxNode> getParseResult() {
		return new ArrayList<HtmlSaxNode>(parseResult);
	}
	
	public void clearParseResult() {
		parseResult = new ArrayList<HtmlSaxNode>();
	}
	
	public void setParseResult(ArrayList<HtmlSaxNode> parseResult) {
		this.parseResult = parseResult;
	}
	
	public void setParseResultBeforeBranching(TreeNode<HtmlSaxNode> parseResultBeforeBranching) {
		this.parseResultBeforeBranching = parseResultBeforeBranching;
	}
	
	public TreeNode<HtmlSaxNode> getParseResultBeforeBranching() {
		return this.parseResultBeforeBranching;
	}
	
	public void setParseResultInOtherBranch(ArrayList<HtmlSaxNode> parseResultInOtherBranch) {
		this.parseResultInOtherBranch = parseResultInOtherBranch;
	}
	
	public void parse(HtmlToken token) {
		String tokenValue = token.getValue();
		PositionRange tokenLocation = token.getLocation();
			
		switch (token.getType()) {
			case OpeningTag: {
				parseResult.add(new HOpenTag(tokenValue, tokenLocation));
				break;
			}
			case ClosingTag: {
				parseResult.add(new HCloseTag(tokenValue, tokenLocation));
				break;
			}
			case Text: {
				if (!tokenValue.trim().isEmpty()) // Remove empty text
					parseResult.add(new HText(tokenValue, tokenLocation));
				break;
			}
			case AttrName: {
				HtmlAttribute attribute = new HtmlAttribute(tokenValue, tokenLocation);
				
				// Handle conditional code
				if (parseResult.isEmpty())
					tryBorrowingOpenTagIfUnavailable();
				
				HOpenTag tag = getLastOpenTag();
				if (tag != null)
					tag.addAttribute(attribute);

				break;
			}
			case AttrValStart: {
				break;
			}
			case AttrValFrag:
			case AttrValue: {
				// Handle conditional code
				if (parseResult.isEmpty())
					tryBorrowingOpenTagIfUnavailable();
				
				HtmlAttribute attribute = getLastAttribute();
				if (attribute != null)
					attribute.addValueFragment(tokenValue, tokenLocation);

				break;
			}
			case AttrValEnd: {
				// Handle conditional code
				if (parseResult.isEmpty())
					tryBorrowingOpenTagIfUnavailable();
				
				HtmlAttribute attribute = getLastAttribute();
				if (attribute != null) {
					HtmlAttributeValue attributeValue = attribute.getAttributeValue();
					attributeValue.unescapePreservingLength(tokenValue.charAt(0)); // Unescape the attribute value
				}

				break;
			}
		}
	}
	
	private void tryBorrowingOpenTagIfUnavailable() {
		if (parseResult.isEmpty() && parseResultBeforeBranching != null) {
			HtmlSaxNode saxNode = new TreeNodeFactory<HtmlSaxNode>().getRightMostNode(parseResultBeforeBranching);
			if (saxNode instanceof HOpenTag) {
				HOpenTag openTag = (HOpenTag) saxNode;
				parseResult.add(openTag);
				
				parseResultBeforeBranching = new TreeNodeFactory<HtmlSaxNode>().removeRightMostNode(parseResultBeforeBranching);
				
				parseResultInOtherBranch.add(openTag.clone());
			}
		}
	}
	
	private HOpenTag getLastOpenTag() {
		if (!parseResult.isEmpty() && parseResult.get(parseResult.size() - 1) instanceof HOpenTag)
			return (HOpenTag) parseResult.get(parseResult.size() - 1);
		else
			return null;
	}
	
	private HtmlAttribute getLastAttribute() {
		HOpenTag tag = getLastOpenTag();
		if (tag != null)
			return tag.getLastAttribute();
		else
			return null;
	}
	
}

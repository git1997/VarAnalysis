package edu.iastate.parsers.html.core;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
import edu.iastate.symex.util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class WriteHtmlDocumentToIfDefs extends HtmlDocumentVisitor {
	
	private StringBuilder strBuilder;
	
	private int depth; // Depth of the current node during traversal for pretty printing
	
	/**
	 * Converts an HtmlDocument to #ifdefs format.
	 */
	public static String convert(HtmlDocument htmlDocument) {
		WriteHtmlDocumentToIfDefs visitor = new WriteHtmlDocumentToIfDefs();
		visitor.visitDocument(htmlDocument);
		return visitor.getResults();
	}
	
	public WriteHtmlDocumentToIfDefs() {
		this.strBuilder = new StringBuilder();
		this.depth = 0;
	}
	
	public String getResults() {
		return strBuilder.toString();
	}
	
	@Override
	public void visitSelect(HtmlSelect htmlSelect) {
		strBuilder.append("#if (" + htmlSelect.getConstraint().toDebugString() + ")" + System.lineSeparator());
		visitNode(htmlSelect.getTrueBranchNode());
		strBuilder.append("#else" + System.lineSeparator());
		visitNode(htmlSelect.getFalseBranchNode());
		strBuilder.append("#endif" + System.lineSeparator());
	}
	
	@Override
	public void visitElement(HtmlElement htmlElement) {
		strBuilder.append(StringUtils.getIndentedTabs(depth));
		strBuilder.append("<" + htmlElement.getType() + "> (" + htmlElement.getOpenTag().toDebugString() + ")" + System.lineSeparator());
		depth++;
	    super.visitElement(htmlElement);
	    depth--;
	    strBuilder.append(StringUtils.getIndentedTabs(depth));
	    strBuilder.append("</" + htmlElement.getType() + ">" + (htmlElement.getCloseTags().size() > 1 ? (" (" + htmlElement.getCloseTags().size() + " CloseTags)") : "") + System.lineSeparator());	
	}
	
	@Override 
	public void visitText(HtmlText htmlText) {
		strBuilder.append(htmlText.toDebugString() + System.lineSeparator());
	}
	
}
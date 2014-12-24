package edu.iastate.parsers.html.core;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;

/**
 * 
 * @author HUNG
 *
 */
public class WriteHtmlDocumentToIfDefs extends HtmlDocumentVisitor {
	
	private StringBuilder strBuilder;
	
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
	}
	
	public String getResults() {
		return strBuilder.toString();
	}
	
	@Override
	public void visitSelect(HtmlSelect htmlSelect) {
		strBuilder.append(System.lineSeparator() + "#if (" + htmlSelect.getConstraint().toDebugString() + ")" + System.lineSeparator());
		visitNode(htmlSelect.getTrueBranchNode());
		strBuilder.append(System.lineSeparator() + "#else" + System.lineSeparator());
		visitNode(htmlSelect.getFalseBranchNode());
		strBuilder.append(System.lineSeparator() + "#endif" + System.lineSeparator());
	}
	
	@Override
	public void visitElement(HtmlElement htmlElement) {
		strBuilder.append("<" + htmlElement.getType() + ">");
	    super.visitElement(htmlElement);
		strBuilder.append("</" + htmlElement.getType() + ">");	
	}
	
}
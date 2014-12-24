package edu.iastate.parsers.html.dom.nodes;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDocumentVisitor {
	
	/**
	 * Visits an HtmlDocument
	 * @param htmlDocument
	 */
	public void visitDocument(HtmlDocument htmlDocument) {
		for (HtmlNode topNode : htmlDocument.getTopNodes())
			visitNode(topNode);
	}

	/**
	 * Visits an HtmlNode
	 * @param htmlNode
	 */
	public void visitNode(HtmlNode htmlNode) {
		if (htmlNode instanceof HtmlElement)
			visitElement((HtmlElement) htmlNode);
		
		else if (htmlNode instanceof HtmlText)
			visitText((HtmlText) htmlNode);
		
		else if (htmlNode instanceof HtmlSelect)
			visitSelect((HtmlSelect) htmlNode);
		
		else if (htmlNode instanceof HtmlConcat)
			visitConcat((HtmlConcat) htmlNode);
		
		else // if (htmlNode instanceof HtmlEmpty)
			visitHtmlEmpty((HtmlEmpty) htmlNode);
	}
	
	public void visitElement(HtmlElement htmlElement) {
		for (HtmlAttribute attribute : htmlElement.getAttributes())
			visitAttribute(attribute);
		for (HtmlNode childNode : htmlElement.getChildNodes())
			visitNode(childNode);
	}
	
	public void visitAttribute(HtmlAttribute htmlAttribute) {
		visitAttributeValue(htmlAttribute.getAttributeValue());
	}
	
	public void visitAttributeValue(HtmlAttributeValue htmlAttributeValue) {
	}
	
	public void visitText(HtmlText htmlText) {
	}
	
	public void visitSelect(HtmlSelect htmlSelect) {
		visitNode(htmlSelect.getTrueBranchNode());
		visitNode(htmlSelect.getFalseBranchNode());
	}
	
	public void visitConcat(HtmlConcat htmlConcat) {
		for (HtmlNode childNode : htmlConcat.getChildNodes())
			visitNode(childNode);
	}
	
	public void visitHtmlEmpty(HtmlEmpty htmlEmpty) {
	}
		
}

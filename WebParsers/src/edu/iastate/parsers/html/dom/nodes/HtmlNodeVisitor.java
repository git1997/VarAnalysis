package edu.iastate.parsers.html.dom.nodes;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlNodeVisitor {

	/**
	 * Visits a general HtmlNode
	 * @param htmlNode
	 */
	public void visit(HtmlNode htmlNode) {
		if (htmlNode instanceof HtmlConcat)
			visitConcat((HtmlConcat) htmlNode);
		
		else if (htmlNode instanceof HtmlSelect)
			visitSelect((HtmlSelect) htmlNode);
		
		else if (htmlNode instanceof HtmlDocument)
			visitDocument((HtmlDocument) htmlNode);
		
		else if (htmlNode instanceof HtmlElement)
			visitElement((HtmlElement) htmlNode);
		
		else if (htmlNode instanceof HtmlText)
			visitText((HtmlText) htmlNode);
		
		else if (htmlNode instanceof HtmlAttribute)
			visitAttribute((HtmlAttribute) htmlNode);
		
		else // if (htmlNode instanceof HtmlAttributeValue)
			visitAttributeValue((HtmlAttributeValue) htmlNode);
	}
	
	public void visitConcat(HtmlConcat htmlConcat) {
		for (HtmlNode childNode : htmlConcat.getChildNodes())
			visit(childNode);
	}
	
	public void visitSelect(HtmlSelect htmlSelect) {
		if (htmlSelect.getTrueBranchNode() != null)
			visit(htmlSelect.getTrueBranchNode());
		if (htmlSelect.getFalseBranchNode() != null)
			visit(htmlSelect.getFalseBranchNode());
	}
	
	public void visitDocument(HtmlDocument htmlDocument) {
		for (HtmlNode childNode : htmlDocument.getChildNodes())
			visit(childNode);
	}
	
	public void visitElement(HtmlElement htmlElement) {
		for (HtmlAttribute attribute : htmlElement.getAttributes())
			visitAttribute(attribute);
		for (HtmlNode childNode : htmlElement.getChildNodes())
			visit(childNode);
	}
	
	public void visitText(HtmlText htmlText) {
	}
	
	public void visitAttribute(HtmlAttribute htmlAttribute) {
		visitAttributeValue(htmlAttribute.getAttributeValue());
	}
	
	public void visitAttributeValue(HtmlAttributeValue htmlAttributeValue) {
	}
		
}

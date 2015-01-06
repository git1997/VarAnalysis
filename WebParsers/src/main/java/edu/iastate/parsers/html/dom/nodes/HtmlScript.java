package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlScript extends HtmlElement {

	public HtmlScript(HOpenTag htmlOpenTag) {
		super(htmlOpenTag);
	}
	
	public HText getSourceCode() {
		StringBuilder sourceCode = new StringBuilder();
		PositionRange location = null;
		
		for (HtmlNode childNode : childNodes) {
			if (childNode instanceof HtmlText) {
				HtmlText text = (HtmlText) childNode;
				sourceCode.append(text.getStringValue());
				if (location == null)
					location = text.getLocation();
				else
					location = new CompositeRange(location, text.getLocation());
			}
			else if (childNode instanceof HtmlSelect) {
				// TODO Implement this case where there are alternatives in the JavaScript code
			}
		}
		
		if (location == null)
			location = Range.UNDEFINED;
		
		return new HText(sourceCode.toString(), location);
	}

}

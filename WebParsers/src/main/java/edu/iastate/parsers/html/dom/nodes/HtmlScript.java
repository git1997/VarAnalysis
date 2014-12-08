package edu.iastate.parsers.html.dom.nodes;

import edu.iastate.parsers.html.sax.nodes.HOpenTag;
import edu.iastate.parsers.html.sax.nodes.HText;
import edu.iastate.symex.position.CompositeRange;
import edu.iastate.symex.position.PositionRange;

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
				sourceCode.append(((HtmlText) childNode).getStringValue());
				if (location == null)
					location = childNode.getLocation();
				else
					location = new CompositeRange(location, childNode.getLocation());
			}
			else if (childNode instanceof HtmlSelect) {
				// TODO Implement this case
			}
		}
		
		return new HText(sourceCode.toString(), location);
	}

}

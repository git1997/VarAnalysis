package edu.iastate.parsers.html.run;

import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;

/**
 * 
 * @author HUNG
 *
 */
public class WriteHtmlDocumentToIfDefs {
	
	/**
	 * Converts an HtmlDocument to #ifdefs format.
	 */
	public static String convert(HtmlDocument htmlDocument) {
		return convertNode(htmlDocument);
	}
	
	/**
	 * Converts an HtmlNode to #ifdefs format.
	 */
	private static String convertNode(HtmlNode htmlNode) {
		if (htmlNode instanceof HtmlDocument) {
			StringBuilder str = new StringBuilder();
	    	for (HtmlNode child : ((HtmlDocument) htmlNode).getChildNodes()) {
	    		String childValue = convertNode(child);
    			str.append(childValue);
    		}
    		return str.toString();
		}
		
		else if (htmlNode instanceof HtmlConcat) {
	    	StringBuilder str = new StringBuilder();
	    	for (HtmlNode child : ((HtmlConcat) htmlNode).getChildNodes()) {
	    		String childValue = convertNode(child);
    			str.append(childValue);
    		}
    		return str.toString();
		}
		
		else if (htmlNode instanceof HtmlSelect) {
			String constraint = ((HtmlSelect) htmlNode).getConstraint().toString();
			
			String trueBranch = convertNode(((HtmlSelect) htmlNode).getTrueBranchNode());
			String falseBranch = convertNode(((HtmlSelect) htmlNode).getFalseBranchNode());
		
			String retString = "\n#if (" + constraint + ")\n"
					+ trueBranch + "\n"
					+ "#else" + "\n"
					+ falseBranch + "\n"
					+ "#endif" + "\n";
			
			return retString;
		}
		
		else if (htmlNode instanceof HtmlElement) {
			HtmlElement element = (HtmlElement) htmlNode;
			StringBuilder str = new StringBuilder();
			str.append("<" + element.getType() + ">");
	    	for (HtmlNode child : element.getChildNodes()) {
	    		String childValue = convertNode(child);
    			str.append(childValue);
    		}
			str.append("</" + element.getType() + ">");
    		return str.toString();
		}
		
		else {
			// TODO Handle other nodes here
			return "";
		}
    }
	
}
package edu.iastate.analysis.references;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlDeclOfHtmlInputValue extends DeclaringReference {
	
	private HtmlInputDecl htmlInputDecl;
	
	private HtmlAttribute htmlAttribute; // Used by edu.iastate.analysis.references.detection.DataFlowManager.resolveDataFlowsFromServerCodeToClientCode(ArrayList<Reference>, HashMap<String, ArrayList<Reference>>)

	/**
	 * Constructor
	 */
	public HtmlDeclOfHtmlInputValue(String name, PositionRange location, HtmlInputDecl htmlInputDecl, HtmlAttribute htmlAttribute) {
		super(name, location);
		this.htmlInputDecl = htmlInputDecl;
		this.htmlAttribute = htmlAttribute;
	}
	
	/**
	 * Returns the HtmlInputDecl of this HtmlDeclOfHtmlInputValue
	 */
	public HtmlInputDecl getHtmlInputDecl() {
		return htmlInputDecl;
	}
	
	/**
	 * Returns the HtmlAttribute of this HtmlDeclOfHtmlInputValue
	 */
	public HtmlAttribute getHtmlAttribute() {
		return htmlAttribute;
	}
	
}

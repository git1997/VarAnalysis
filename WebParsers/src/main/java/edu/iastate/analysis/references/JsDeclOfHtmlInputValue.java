package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsDeclOfHtmlInputValue extends JsObjectFieldDecl {

	/**
	 * Constructor
	 */
	public JsDeclOfHtmlInputValue(String name, PositionRange location, JsRefToHtmlInput jsRefToHtmlInput) {
		super(name, location, jsRefToHtmlInput);
	}
	
	public JsRefToHtmlInput getJsRefToHtmlInput() {
		return (JsRefToHtmlInput) object;
	}
	
}

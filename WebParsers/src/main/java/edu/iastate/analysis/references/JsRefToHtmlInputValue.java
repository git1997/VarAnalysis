package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlInputValue extends JsObjectFieldRef {

	/**
	 * Constructor
	 */
	public JsRefToHtmlInputValue(String name, PositionRange location, JsRefToHtmlInput jsRefToHtmlInput) {
		super(name, location, jsRefToHtmlInput);
	}
	
}

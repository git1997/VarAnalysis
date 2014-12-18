package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlForm extends JsObjectFieldRef {

	/**
	 * Constructor
	 */
	public JsRefToHtmlForm(String name, PositionRange location, RegularReference object) {
		super(name, location, object);
	}

}

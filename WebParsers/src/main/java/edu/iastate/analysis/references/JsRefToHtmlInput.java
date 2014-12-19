package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlInput extends JsObjectFieldRef {

	/**
	 * Constructor
	 */
	public JsRefToHtmlInput(String name, PositionRange location, JsRefToHtmlForm jsRefToHtmlForm) {
		super(name, location, jsRefToHtmlForm);
	}
	
	public JsRefToHtmlForm getJsRefToHtmlForm() {
		return (JsRefToHtmlForm) object;
	}
	
	public String getFormName() {
		return getJsRefToHtmlForm().getName();
	}
	
}

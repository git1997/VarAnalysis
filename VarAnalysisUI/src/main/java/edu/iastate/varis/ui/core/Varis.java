package edu.iastate.varis.ui.core;

import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class Varis {
	
	private static boolean varisEnabled = false;
	
	public static boolean varisEnabled() {
		return varisEnabled;
	}
	
	public static void enableVaris() {
		varisEnabled = true;
		
		UIHelper.getActiveEditor().setFocus();
	}
	
	public static void disableVaris() {
		varisEnabled = false;
		
		UIHelper.getActiveEditor().setFocus();
	}

}

package edu.iastate.varis.ui.core;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.varis.ui.highlighters.SemanticHighlightingManager;
import edu.iastate.varis.ui.hyperlinks.HyperlinkManager;

/**
 * 
 * @author HUNG
 *
 */
public class VarisManager {

	private static VarisManager instance = new VarisManager();

	private boolean isEnabled = false;
	
	public static VarisManager getInstance() {
		return instance;
	}
	
	public void enable() {
		this.isEnabled = true;
	}
	
	public void disable() {
		this.isEnabled = false;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void addHtmlDocument(HtmlDocument htmlDocument) {
		SemanticHighlightingManager.getInstance().addHtmlDocument(htmlDocument);
		HyperlinkManager.getInstance().addHtmlDocument(htmlDocument);
	}
	
	public void removeHtmlDocuments() {
		SemanticHighlightingManager.getInstance().removeHtmlDocuments();
		HyperlinkManager.getInstance().removeHtmlDocuments();
	}
	
}

package edu.iastate.varis.ui.highlighters;

import java.io.File;
import java.util.LinkedList;
import org.eclipse.jface.text.Position;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttrNameHighlighting extends AbstractSemanticHighlighting {

	public HtmlAttrNameHighlighting() {
		super();
		getPreferenceStore().setDefault(getColorPreferenceKey(), "#8F007E"); //RBG(143, 0, 126) http://www.yellowpipe.com/yis/tools/hex-to-rgb/color-converter.php
	}
	
	public LinkedList<Position> consumes(File file, int offset, int length) {
		return SemanticHighlightingManager.getInstance().getHtmlAttrNamePositions(file);
	}

}

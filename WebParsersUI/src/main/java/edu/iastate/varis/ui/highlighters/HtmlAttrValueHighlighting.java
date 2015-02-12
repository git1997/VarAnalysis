package edu.iastate.varis.ui.highlighters;

import java.io.File;
import java.util.LinkedList;
import org.eclipse.jface.text.Position;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlAttrValueHighlighting extends AbstractSemanticHighlighting {

	public HtmlAttrValueHighlighting() {
		super();
		getPreferenceStore().setDefault(getColorPreferenceKey(), "#5500FF"); //RBG(85, 0, 255) http://www.yellowpipe.com/yis/tools/hex-to-rgb/color-converter.php
		getPreferenceStore().setDefault(getItalicPreferenceKey(), true);
	}
	
	public LinkedList<Position> consumes(File file, int offset, int length) {
		return SemanticHighlightingManager.getInstance().getHtmlAttrValuePositions(file);
	}

}

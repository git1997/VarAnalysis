package edu.iastate.varis.ui.highlighters;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.jface.text.Position;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlTagNameHighlighting extends AbstractSemanticHighlighting {

	public HtmlTagNameHighlighting() {
		super();
		getPreferenceStore().setDefault(getColorPreferenceKey(), "#1B8281"); //RBG(27, 130, 129) http://www.yellowpipe.com/yis/tools/hex-to-rgb/color-converter.php
	}
	
	public LinkedList<Position> consumes(File file, int offset, int length) {
		return SemanticHighlightingManager.getInstance().getHtmlTagNamePositions(file);
	}

}

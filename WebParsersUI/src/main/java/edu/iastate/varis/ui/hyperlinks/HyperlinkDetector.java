package edu.iastate.varis.ui.hyperlinks;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import edu.iastate.symex.ui.UIHelper;
import edu.iastate.varis.ui.core.VarisManager;

/**
 * 
 * @author HUNG
 *
 */
public class HyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (!VarisManager.getInstance().isEnabled())
			return null;
		
		File file = UIHelper.iFileToFile(UIHelper.getActiveEditorFile());
		ArrayList<Hyperlink> hyperlinks = HyperlinkManager.getInstance().detectHyperlinks(file, region);
		
		if (hyperlinks.isEmpty())
			return null;
		else
			return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}

}

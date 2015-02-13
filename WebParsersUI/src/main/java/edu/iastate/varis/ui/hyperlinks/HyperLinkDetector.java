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
public class HyperLinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (!VarisManager.getInstance().isEnabled())
			return null;
		
		File file = UIHelper.iFileToFile(UIHelper.getActiveEditorFile());
		ArrayList<Hyperlink> hyperlinks = new ArrayList<Hyperlink>();
		
		for (Hyperlink hyperlink : HyperlinkManager.getInstance().getHyperlinks(file)) {
			if (regionOverlapped(region, hyperlink.getHyperlinkRegion()))
				hyperlinks.add(hyperlink);
		}
		
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}
	
	private boolean regionOverlapped(IRegion region1, IRegion region2) {
		return (region1.getOffset() + region1.getLength() >= region2.getOffset()
				&& region1.getOffset() <= region2.getOffset() + region2.getLength());
	}

}

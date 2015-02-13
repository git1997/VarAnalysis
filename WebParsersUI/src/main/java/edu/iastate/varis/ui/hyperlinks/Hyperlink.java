package edu.iastate.varis.ui.hyperlinks;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class Hyperlink implements IHyperlink {

	private Range fromLocation;
	private Range toLocation;

	public Hyperlink(Range fromLocation, Range toLocation) {
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
	}
		
	@Override
	public void open() {
		UIHelper.selectAndReveal(toLocation.getFile(), toLocation.getOffset(), toLocation.getLength());
	}

	@Override
	public String getTypeLabel() {
		return "TODO: Hyperlink.java @ getTypeLabel";
	}

	@Override
	public String getHyperlinkText() {
		return toLocation.getStartPosition().toDebugString();
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return new Region(fromLocation.getOffset(), fromLocation.getLength());
	}

}

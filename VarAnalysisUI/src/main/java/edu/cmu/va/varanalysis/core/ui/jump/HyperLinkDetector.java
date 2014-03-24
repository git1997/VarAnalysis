package edu.cmu.va.varanalysis.core.ui.jump;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import edu.cmu.va.varanalysis.model.CallGraph;
import edu.cmu.va.varanalysis.model.Edge;
import edu.cmu.va.varanalysis.model.PositionRange;
import edu.cmu.va.varanalysis.model.Range;
import edu.cmu.va.varanalysis.model.SymExModel;
import edu.cmu.va.varanalysis.ui.Util;

public class HyperLinkDetector extends AbstractHyperlinkDetector {

	public HyperLinkDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		IFile file = new Util().getFile();
		if (file == null)
			return null;

		CallGraph cg = SymExModel.getInstance().getCallGraph(file);

		List<IHyperlink> result = new ArrayList<>();
		if (cg == null)
			return null;

		for (Edge e : cg.getEdges()) {
			if (matchNode(e.getFrom(), region))
				result.add(new HTMLLink(e.getFrom()));
			if (matchNode(e.getTo(), region)) 
				result.add(new HTMLLink(e.getTo()));
		}

		if (result.isEmpty()) return null;
		return result.toArray(new IHyperlink[0]);
	}

	private boolean matchNode(PositionRange from, IRegion region) {
		Range r = from.getRanges().get(0);
		// TODO check file
		return (r.getFrom() <= region.getOffset())
				&& (r.getTo() >= region.getOffset() + region.getLength());
	}

	private static class HTMLLink implements IHyperlink {

		private PositionRange range;

		public HTMLLink(PositionRange range) {
			this.range = range;
		}

		@Override
		public void open() {
			// TODO Auto-generated method stub

		}

		@Override
		public String getTypeLabel() {
			return "HTML Call Graph Edge";
		}

		@Override
		public String getHyperlinkText() {
			return "link";
		}

		@Override
		public IRegion getHyperlinkRegion() {
			return toRegion(range);
		}

		private IRegion toRegion(final PositionRange range) {
			return new IRegion() {

				@Override
				public int getOffset() {
					return range.getRanges().get(0).getFrom();
				}

				@Override
				public int getLength() {
					return range.getRanges().get(0).getTo()
							- range.getRanges().get(0).getFrom();
				}
			};
		}
	}
}

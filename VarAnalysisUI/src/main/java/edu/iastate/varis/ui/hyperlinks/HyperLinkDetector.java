package edu.iastate.varis.ui.hyperlinks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

import edu.cmu.va.varanalysis.model.CallGraph;
import edu.cmu.va.varanalysis.model.Edge;
import edu.cmu.va.varanalysis.model.PositionRange;
import edu.cmu.va.varanalysis.model.Range;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.varis.ui.core.Varis;

public class HyperLinkDetector extends AbstractHyperlinkDetector {

	public HyperLinkDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		
		if (!Varis.varisEnabled())
			return null;
		
		/*
		IFile file = new Util().getFile();
		if (file == null)
			return null;

		CallGraph cg = SymExModel.getInstance().getCallGraph(file);
		*/
		
		CallGraph cg = getCallGraph();
		List<IHyperlink> result = new ArrayList<>();
		if (cg == null)
			return null;

		for (Edge e : cg.getEdges()) {
			if (matchNode(e.getFrom(), region))
				result.add(new HTMLLink(e.getFrom(), e.getTo()));
			//if (matchNode(e.getTo(), region)) 
			//	result.add(new HTMLLink(e.getTo()));
		}

		if (result.isEmpty()) return null;

		return result.toArray(new IHyperlink[0]);
	}
	
	public CallGraph getCallGraph() {
		String file = "/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/index.php";
		
		CallGraph cg = new CallGraph();
		cg.addEdge(new PositionRange(file, 36, 41), new PositionRange(file, 278, 285), FeatureExprFactory.True());
		cg.addEdge(new PositionRange(file, 149, 157), new PositionRange(file, 355, 363), FeatureExprFactory.True());
		cg.addEdge(new PositionRange(file, 210, 218), new PositionRange(file, 408, 416), FeatureExprFactory.True());
		
		return cg;
	}

	private boolean matchNode(PositionRange from, IRegion region) {
		Range r = from.getRanges().get(0);
		// TODO check file
		return (r.getFrom() <= region.getOffset())
				&& (r.getTo() >= region.getOffset() + region.getLength());
	}

	public static class HTMLLink implements IHyperlink {

		private PositionRange positionRangeFrom;
		private PositionRange positionRangeTo;

		public HTMLLink(PositionRange positionRangeFrom, PositionRange positionRangeTo) {
			this.positionRangeFrom = positionRangeFrom;
			this.positionRangeTo = positionRangeTo;
		}
		
		@Override
		public void open() {
			// TODO Auto-generated method stub
			//MessageDialog.openInformation(null,  "hey", "good");
			//IRegion region = toRegion(new PositionRange("", 50, 60));
			//fOpenAction.run(new TextSelection(region.getOffset(), region.getLength()));
			Range range = positionRangeTo.getRanges().get(0);
			UIHelper.selectAndReveal(new File(range.getFile()), range.getFrom(), range.getTo() - range.getFrom());
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
			return toRegion(positionRangeFrom);
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

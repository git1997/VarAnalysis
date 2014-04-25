package edu.iastate.varis.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import edu.cmu.va.varanalysis.model.CallGraph;
import edu.cmu.va.varanalysis.model.Edge;
import edu.cmu.va.varanalysis.model.Range;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.varis.ui.core.Varis;
import edu.iastate.varis.ui.hyperlinks.HyperLinkDetector;

/**
 * 
 * @author HUNG
 *
 */
public class JumpToDeclaration implements IEditorActionDelegate {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action ) {
		if (UIHelper.saveAllEditors()) {
			jump();
		}
	}
	
	/**
	 * Opens the Rename wizard.
	 */
	private void jump() {
			if (!Varis.varisEnabled())
				return;
			
			/*
			IFile file = new Util().getFile();
			if (file == null)
				return null;

			CallGraph cg = SymExModel.getInstance().getCallGraph(file);
			*/
			HyperLinkDetector detector = new HyperLinkDetector();
			
			CallGraph cg = detector.getCallGraph();
			List<IHyperlink> result = new ArrayList<>();
			if (cg == null)
				return;

			int currentOffset = UIHelper.getActiveEditorStyledText().getCaretOffset();
			for (Edge e : cg.getEdges()) {
				Range r = e.getFrom().getRanges().get(0);
				if (r.getFrom() <= currentOffset && currentOffset <= r.getTo())
					result.add(new HyperLinkDetector.HTMLLink(e.getFrom(), e.getTo()));
			}

			if (result.isEmpty()) 
				return;

			result.get(0).open();
	}

}

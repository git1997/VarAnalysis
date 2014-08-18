package edu.iastate.parsers.ui.actions;

import java.io.File;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.parsers.ui.views.WebAnalysisView;
import edu.iastate.symex.position.Position;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeForwardSlice implements IEditorActionDelegate {

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
			File file = UIHelper.iFileToFile(UIHelper.getActiveEditorFile());
			int offset = UIHelper.getActiveEditorStyledText().getCaretOffset();
			Position position = new Position(file, offset);
			
			WebAnalysisView.inst.displayForwardSlice(position);
		}
	}
	
}

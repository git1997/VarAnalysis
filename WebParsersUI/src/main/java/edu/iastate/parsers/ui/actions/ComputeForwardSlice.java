package edu.iastate.parsers.ui.actions;

import java.io.File;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import edu.iastate.parsers.ui.views.WebAnalysisView;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeForwardSlice implements IEditorActionDelegate {

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {
		// TODO Auto-generated method stub
		
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
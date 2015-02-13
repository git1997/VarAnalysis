package edu.iastate.varis.ui.actions;

import java.io.File;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.varis.ui.core.VarisManager;
import edu.iastate.varis.ui.hyperlinks.Hyperlink;
import edu.iastate.varis.ui.hyperlinks.HyperlinkManager;

/**
 * 
 * @author HUNG
 *
 */
public class JumpToDeclaration implements IEditorActionDelegate {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action ) {
		if (VarisManager.getInstance().isEnabled() && UIHelper.saveAllEditors()) {
			jumpToDeclaration();
		}
	}
	
	/**
	 * Jumps to declaration.
	 */
	private void jumpToDeclaration() {
		File file = UIHelper.iFileToFile(UIHelper.getActiveEditorFile());
		int caretOffset = UIHelper.getActiveEditorStyledText().getCaretOffset();
		Region region = new Region(caretOffset, 0);
		
		for (Hyperlink hyperlink : HyperlinkManager.getInstance().detectHyperlinks(file, region)) {
			hyperlink.open();
			return;
		}
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {
		// TODO Auto-generated method stub
		
	}

}

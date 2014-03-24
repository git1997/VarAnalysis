package edu.cmu.va.varanalysis.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

import symexui.Activator;

public class Util {
	private IFile file;

	public IFile getFile() {// region.getParentDocument().get()
		file = null;
		// resolve current sourceModule
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = Activator.getActivePage();
				if (page != null) {
					IEditorPart editor = page.getActiveEditor();
					IEditorInput input = editor.getEditorInput();
					file = (IFile) input.getAdapter(IFile.class);
				}
			}
		});

		return file;
	}
}

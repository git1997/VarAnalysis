package edu.iastate.parsers.ui;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class UIHelper2 {
	
	private static ISourceModule sourceModule;
	
	public static File getActiveFileOrNull() {
		sourceModule = null;
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PHPUiPlugin.getActivePage();
				if (page != null) {
					IEditorPart editor = page.getActiveEditor();
					if (editor instanceof PHPStructuredEditor) {
						PHPStructuredEditor phpStructuredEditor = (PHPStructuredEditor) editor;
						if (phpStructuredEditor.getTextViewer() != null && phpStructuredEditor != null) {
							sourceModule = (ISourceModule) phpStructuredEditor.getModelElement();
						}
					}
				}
			}
		});
		
		if (sourceModule != null) {
			IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(sourceModule.getPath());
			return UIHelper.iFileToFile(iFile);
		}
		else
			return null;
	}
	
}

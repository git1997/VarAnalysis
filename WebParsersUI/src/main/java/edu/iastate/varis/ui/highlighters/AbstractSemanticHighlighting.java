package edu.iastate.varis.ui.highlighters;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.internal.ui.preferences.PreferenceConstants;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension2;

import edu.iastate.parsers.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractSemanticHighlighting implements ISemanticHighlighting, ISemanticHighlightingExtension2 {
	
	private IPreferenceStore preferenceStore = PreferenceConstants.getPreferenceStore();
	
	private final String preferenceKey = this.getClass().getName();
	
	private ISourceModule sourceModule = null;
	
	/**
	 * Constructor
	 */
	public AbstractSemanticHighlighting() {
		preferenceStore.setDefault(getEnabledPreferenceKey(), true);
	}
	
	public Position[] consumes(IStructuredDocumentRegion region) {
		if (region.getStart() != 0)
			return new Position[0];
		
		resolveSourceModule();
		if (sourceModule == null)
			return new Position[0];
		
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(sourceModule.getPath());
		File file = UIHelper.iFileToFile(iFile);
		
		LinkedList<Position> positions = consumes(file, region.getStart(), region.getLength());
		return positions.toArray(new Position[positions.size()]);
	}
	
	public abstract LinkedList<Position> consumes(File file, int offset, int length);
	
	private void resolveSourceModule() {
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
	}

	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}
	
	public String getPreferenceKey() {
		return preferenceKey;
	}

	@Override
	public String getDisplayName() {
		return this.getClass().getName();
	}

	@Override
	public String getBackgroundColorPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BGCOLOR_SUFFIX;
	}

	public String getBoldPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_BOLD_SUFFIX;
	}

	public String getColorPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_COLOR_SUFFIX;
	}

	public String getEnabledPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ENABLED_SUFFIX;
	}

	public String getItalicPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ITALIC_SUFFIX;
	}

	public String getStrikethroughPreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_STRIKETHROUGH_SUFFIX;
	}

	public String getUnderlinePreferenceKey() {
		return PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_PREFIX
				+ preferenceKey
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_UNDERLINE_SUFFIX;
	}

}

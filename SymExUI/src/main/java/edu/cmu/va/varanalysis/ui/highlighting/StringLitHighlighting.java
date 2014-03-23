package edu.cmu.va.varanalysis.ui.highlighting;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jdt.internal.ui.preferences.MockupPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.ui.PHPUiPlugin;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.php.ui.editor.SharedASTProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension2;

import edu.cmu.va.varanalysis.model.SymExModel;
import symexui.Activator;

public class StringLitHighlighting implements ISemanticHighlighting,
		ISemanticHighlightingExtension2 {

	public static final String SEMANTICHIGHLIGHTING_SYMEX_BOLD = "semantichighlighting.symex.bold";
	public static final String SEMANTICHIGHLIGHTING_SYMEX_COLOR = "semantichighlighting.symex.color";
	public static final String SEMANTICHIGHLIGHTING_SYMEX_BACKGROUND = "semantichighlighting.symex.background";
	public static final String SEMANTICHIGHLIGHTING_SYMEX_ENDABLED = "semantichighlighting.symex.endabled";

	@Override
	public String getBackgroundColorPreferenceKey() {
		return SEMANTICHIGHLIGHTING_SYMEX_BACKGROUND;
	}

	@Override
	public Position[] consumes(IStructuredDocumentRegion region) {

		IFile file = getFile(region);
		if (file == null)
			return new Position[0];

		List<edu.cmu.va.varanalysis.model.Position> positions = SymExModel
				.getInstance().getStringLits(file, region.getStartOffset(),
						region.getEndOffset());

		Position[] result = new Position[positions.size()];
		for (int i = 0; i < positions.size(); i++)
			result[i] = new Position(positions.get(i).getFrom(), positions.get(
					i).getTo());
		
		return result;
	}

	@Override
	public String getBoldPreferenceKey() {
		// TODO Auto-generated method stub
		return SEMANTICHIGHLIGHTING_SYMEX_BOLD;
	}

	@Override
	public String getColorPreferenceKey() {
		// TODO Auto-generated method stub
		return SEMANTICHIGHLIGHTING_SYMEX_COLOR;
	}

	@Override
	public String getDisplayName() {
		return "SymEx String Literals";
	}

	@Override
	public String getEnabledPreferenceKey() {
		return SEMANTICHIGHLIGHTING_SYMEX_ENDABLED;
	}

	@Override
	public String getItalicPreferenceKey() {
		return "semantichighlighting.symex.italic";
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public String getStrikethroughPreferenceKey() {
		return "semantichighlighting.symex.strikethrough";
	}

	@Override
	public String getUnderlinePreferenceKey() {
		return "semantichighlighting.symex.underline";
	}

	private IFile file;

	protected IFile getFile(final IStructuredDocumentRegion region) {// region.getParentDocument().get()
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

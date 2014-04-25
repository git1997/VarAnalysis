package edu.cmu.va.varanalysis.ui.highlighting;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension2;

import edu.cmu.va.varanalysis.model.SymExModel;
import edu.cmu.va.varanalysis.symexui.Activator;
import edu.cmu.va.varanalysis.ui.Util;

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

		IFile file =new Util().  getFile();
		if (file == null)
			return new Position[0];

		List<edu.cmu.va.varanalysis.model.Position> positions = SymExModel
				.getInstance().getStringLits(file, region.getStartOffset(),
						region.getEndOffset());

		Position[] result = new Position[positions.size()];
		for (int i = 0; i < positions.size(); i++)
			result[i] = new Position(positions.get(i).getFrom(), positions.get(
					i).getTo()
					- positions.get(i).getFrom());

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

	

}

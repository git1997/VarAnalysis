package edu.cmu.va.varanalysis.ui.highlighting;

import java.util.ArrayList;
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

import symexui.Activator;
import edu.cmu.va.varanalysis.model.CallGraph;
import edu.cmu.va.varanalysis.model.PositionRange;
import edu.cmu.va.varanalysis.model.Range;
import edu.cmu.va.varanalysis.model.SymExModel;
import edu.cmu.va.varanalysis.ui.Util;

public class CallGraphNodeHighlighting implements ISemanticHighlighting,
		ISemanticHighlightingExtension2 {

	public static final String SEMANTICHIGHLIGHTING_CGNODE_UNDERLINE = "semantichighlighting.cgnode.underline";
	public static final String SEMANTICHIGHLIGHTING_CGNODE_BOLD = "semantichighlighting.cgnode.bold";
	public static final String SEMANTICHIGHLIGHTING_CGNODE_COLOR = "semantichighlighting.cgnode.color";
	public static final String SEMANTICHIGHLIGHTING_CGNODE_BACKGROUND = "semantichighlighting.cgnode.background";
	public static final String SEMANTICHIGHLIGHTING_CGNODE_ENDABLED = "semantichighlighting.cgnode.endabled";

	@Override
	public String getBackgroundColorPreferenceKey() {
		return SEMANTICHIGHLIGHTING_CGNODE_BACKGROUND;
	}

	@Override
	public Position[] consumes(IStructuredDocumentRegion region) {

		IFile file = new Util().getFile();
		if (file == null)
			return new Position[0];

		CallGraph cg = SymExModel.getInstance().getCallGraph(file);

		List<Position> result = new ArrayList<>();
		if (cg != null) {
			List<PositionRange> nodes = cg.getNodes();
			result.addAll(findRanges(nodes, file, region.getStartOffset(),
					region.getEndOffset()));
		}

		return result.toArray(new Position[0]);
	}

	private List<Position> findRanges(List<PositionRange> nodes, IFile file,
			int regFrom, int regTo) {
		List<Position> result = new ArrayList<>();
		for (PositionRange node : nodes)
			for (Range r : node.getRanges())
			// if (file.getName().equals(r.getFile()))// TODO fix
			{

				int from = r.getFrom();
				int to = r.getTo();

				if ((from >= regFrom && from < regTo)
						|| (to > regFrom && to <= regTo)) {
					int offset = Math.max(from, regFrom);
					result.add(new Position(offset, Math.min(to, regTo)
							- offset));
				}
			}

		return result;
	}

	@Override
	public String getBoldPreferenceKey() {
		// TODO Auto-generated method stub
		return SEMANTICHIGHLIGHTING_CGNODE_BOLD;
	}

	@Override
	public String getColorPreferenceKey() {
		// TODO Auto-generated method stub
		return SEMANTICHIGHLIGHTING_CGNODE_COLOR;
	}

	@Override
	public String getDisplayName() {
		return "Call Graph Nodes";
	}

	@Override
	public String getEnabledPreferenceKey() {
		return SEMANTICHIGHLIGHTING_CGNODE_ENDABLED;
	}

	@Override
	public String getItalicPreferenceKey() {
		return "semantichighlighting.cgnode.italic";
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	public String getStrikethroughPreferenceKey() {
		return "semantichighlighting.cgnode.strikethrough";
	}

	@Override
	public String getUnderlinePreferenceKey() {
		return SEMANTICHIGHLIGHTING_CGNODE_UNDERLINE;
	}

}

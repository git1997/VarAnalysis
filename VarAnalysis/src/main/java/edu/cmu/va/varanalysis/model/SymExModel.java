package edu.cmu.va.varanalysis.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;

import de.fosd.typechef.parser.html.VarDom;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

public class SymExModel {

	private static final SymExModel instance = new SymExModel();

	public static SymExModel getInstance() {
		return instance;
	}

	private SymExModel() {
	}

	private final HashMap<IFile, DataNode> models = new HashMap<>();
	private final HashMap<IFile, VarDom> vardoms = new HashMap<>();
	private final HashMap<IFile, CallGraph> callgraphs = new HashMap<>();
	private final List<SymExModelChangeListener> listeners = new ArrayList<>();

	public void updateDModel(IFile file, DataNode model) {
		if (model == null)
			models.remove(file);
		else
			models.put(file, model);
		fireDModelChanged(file);
	}

	public void updateVarDom(IFile file, VarDom model) {
		if (model == null)
			vardoms.remove(file);
		else
			vardoms.put(file, model);
		fireVarDomChanged(file);
	}
	
	public void updateCallGraph(IFile file, CallGraph model) {
		if (model == null)
			callgraphs.remove(file);
		else
			callgraphs.put(file, model);
//		fireVarDomChanged(file);
	}

	private void fireVarDomChanged(IFile file) {
	}

	public boolean isStringLit(IFile file, final int offset, final int length) {
		DataNode model = models.get(file);
		if (model == null)
			return false;

		SearchStringLitVisitor vis = new SearchStringLitVisitor(offset, length);
		model.accept(vis);
		return vis.foundIt;
	}

	class SearchStringLitVisitor extends DataModelVisitor {
		private int length;
		private int offset;

		SearchStringLitVisitor(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		boolean foundIt = false;

		@Override
		public void visitLiteralNode(LiteralNode literalNode) {
			int litpos = literalNode.getPositionRange().getStartPosition().getOffset();
			foundIt = foundIt || (litpos >= offset && litpos < offset + length);
			super.visitLiteralNode(literalNode);
		}
	};

	public void addModelChangeListener(SymExModelChangeListener listener) {
		listeners.add(listener);
	}

	protected void fireDModelChanged(IFile file) {
		for (SymExModelChangeListener l : listeners)
			l.modelUpdated(file);
	}

	public List<Position> getStringLits(IFile file) {
		return getStringLits(file, 0, Integer.MAX_VALUE);
	}

	public List<Position> getStringLits(IFile file, final int from, final int to) {
		DataNode model = models.get(file);
		if (model == null)
			return Collections.EMPTY_LIST;
		final List<Position> result = new ArrayList<>();

		model.accept(new DataModelVisitor() {
			@Override
			public void visitLiteralNode(LiteralNode literalNode) {
				int litpos = literalNode.getPositionRange().getStartPosition().getOffset();
				int length = literalNode.getStringValue().length();

				int end = litpos + length;

				if ((litpos >= from && litpos < to)
						|| (end > from && end <= to))
					result.add(new Position(Math.max(from, litpos), Math.min(
							to, end)));
				super.visitLiteralNode(literalNode);
			}
		});
		return result;
	}

	public DataNode getDModel(IFile file) {
		return models.get(file);
	}
	
	public VarDom getVarDOM(IFile file) {
		return vardoms.get(file);
	}

	public CallGraph getCallGraph(IFile file) {
		return callgraphs.get(file);
	}
}

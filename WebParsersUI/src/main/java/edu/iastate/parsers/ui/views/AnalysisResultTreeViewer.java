package edu.iastate.parsers.ui.views;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.views.GenericTreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class AnalysisResultTreeViewer extends GenericTreeViewer {
	
	private boolean forwardSliceEnabled;
	private ReferenceManager referenceManager = null;
	
	public AnalysisResultTreeViewer(Composite parent, int style, boolean forwardSlice) {
		super(parent, style);
		this.forwardSliceEnabled = forwardSlice;
	}
	
	public void setInput(ReferenceManager referenceManager) {
		this.referenceManager = referenceManager; // Must be set first
		super.setInput(referenceManager);
	}
	
	@Override
	public Object[] getRootNodes(Object input) {
		return ((ReferenceManager) input).getSortedReferenceListByTypeThenNameThenPosition().toArray(new Object[]{});
	}

	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Object> children = new ArrayList<Object>();
		
		if (element instanceof Reference) {
			if (forwardSliceEnabled) {
				children.addAll(referenceManager.getDataFlowManager().getDataFlowFrom((Reference) element));
			}
			else
				children.addAll(referenceManager.getDataFlowManager().getDataFlowTo((Reference) element));
		}
		
		else {
			// Other nodes have no children.
		}
		
		Object[] objects = children.toArray(new Object[]{});
		return objects;
	}

	@Override
	public String getTreeNodeLabel(Object element) {
		return element.getClass().getSimpleName();
	}

	@Override
	public Image getTreeNodeIcon(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}

	@Override
	public String getTreeNodeDescription(Object element) {
		if (element instanceof Reference)
			return ((Reference) element).toDebugString();
		else
			return "";
	}

	@Override
	public PositionRange getTreeNodePositionRange(Object element) {
		if (element instanceof Reference)
			return ((Reference) element).getLocation();
		else
			return Range.UNDEFINED;
	}
	
}

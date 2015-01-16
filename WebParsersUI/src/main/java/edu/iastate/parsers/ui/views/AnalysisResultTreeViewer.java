package edu.iastate.parsers.ui.views;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByName;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByPosition;
import edu.iastate.analysis.references.Reference.ReferenceComparatorByType;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.ui.views.ITreeViewer;

/**
 * 
 * @author HUNG
 *
 */
public class AnalysisResultTreeViewer implements ITreeViewer {
	
	private boolean forwardSliceEnabled;
	
	private ReferenceManager referenceManager = null;
	
	/**
	 * Constructor
	 * @param forwardSlice
	 */
	public AnalysisResultTreeViewer(boolean forwardSlice) {
		this.forwardSliceEnabled = forwardSlice;
	}
	
	@Override
	public Object[] getRootNodes(Object input) {
		referenceManager = ((ReferenceManager) input);
		return referenceManager.getSortedReferenceListByTypeThenNameThenPosition().toArray(new Object[]{});
	}

	@Override
	public Object[] getChildren(Object element) {
		ArrayList<Reference> children = new ArrayList<Reference>();
		
		if (element instanceof Reference) {
			if (forwardSliceEnabled) {
				children.addAll(referenceManager.getDataFlowManager().getDataFlowFrom((Reference) element));
			}
			else
				children.addAll(referenceManager.getDataFlowManager().getDataFlowTo((Reference) element));
			Collections.sort(children, new Reference.ReferenceComparator(new ReferenceComparatorByType(), new ReferenceComparatorByName(), new ReferenceComparatorByPosition()));
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
	public PositionRange getTreeNodeLocation(Object element) {
		if (element instanceof Reference)
			return ((Reference) element).getLocation();
		else
			return Range.UNDEFINED;
	}
	
}

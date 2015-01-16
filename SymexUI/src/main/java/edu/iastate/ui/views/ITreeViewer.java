package edu.iastate.ui.views;

import org.eclipse.swt.graphics.Image;
import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public interface ITreeViewer {

	public Object[] getRootNodes(Object input);

	public Object[] getChildren(Object element);

	public String getTreeNodeLabel(Object element);

	public Image getTreeNodeIcon(Object element);

	public String getTreeNodeDescription(Object element);

	public PositionRange getTreeNodeLocation(Object element);
	
}

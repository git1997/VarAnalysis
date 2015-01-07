package edu.iastate.symex.ui.views;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public abstract class GenericTreeViewer extends TreeViewer {
	
	public GenericTreeViewer(Composite parent, int style) {
		super(parent, style);
		
		/*
		 * Layout
		 */
		getTree().setHeaderVisible(true);
		getTree().setLinesVisible(true);
		//getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		/*
		 * Event handling
		 */
		addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement(); 
				if (selectedElement != null)
					treeNodeSelected(selectedElement);
			}
	    });
		
		/*
		 * Content provider
		 */
		setContentProvider(new MyContentProvider(this));
		setInput(null);
		
		/*
		 * Label provider
		 */
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Tree");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return getTreeNodeLabel(element);
			}
			
			public Image getImage(Object element) {
				return getTreeNodeIcon(element);
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Text");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTreeNodeDescription(element);
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTreeNodeFilePath(element);
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTreeNodeLineRange(element);
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTreeNodeOffsetRange(element);
			}
		});
	}
	
	/*
	 * Event handling
	 */
	
	public void treeNodeSelected(Object element) {
		Range range = getTreeNodePositionFirstRange(element);
		if (!range.isUndefined()) {
			UIHelper.selectAndReveal(range.getFile(), range.getOffset(), range.getLength());
			getControl().setFocus();
		}
	}
	
	/*
	 * Content provider
	 */
	
	private class MyContentProvider implements ITreeContentProvider {
		
		private GenericTreeViewer treeViewer;
		//private HashMap<Object, Object> childToParentMap = new HashMap<Object, Object>();
		
		public MyContentProvider(GenericTreeViewer treeViewer) {
			this.treeViewer = treeViewer;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		@Override
		public Object[] getChildren(Object arg0) {
			Object[] children = treeViewer.getChildren(arg0);
			//for (Object child : children)
			//	childToParentMap.put(arg0, child);
			return children;
		}

		@Override
		public Object[] getElements(Object arg0) {
			return treeViewer.getRootNodes(arg0);
		}

		@Override
		public Object getParent(Object arg0) {
			return null; //childToParentMap.get(arg0);
		}

		@Override
		public boolean hasChildren(Object arg0) {
			return getChildren(arg0).length > 0;
		}
		
	}
	
	public abstract Object[] getRootNodes(Object input);
	
	public abstract Object[] getChildren(Object element);
	
	/*
	 * Label provider
	 */
	
	public abstract String getTreeNodeLabel(Object element);
	
	public abstract Image getTreeNodeIcon(Object element);
	
	public abstract String getTreeNodeDescription(Object element);
	
	public String getTreeNodeFilePath(Object element) {
		Range range = getTreeNodePositionFirstRange(element);
		return range.isUndefined() ? "" : UIHelper.standardizeFilePath(range.getFilePath());
	}
	
	public String getTreeNodeLineRange(Object element) {
		Range range = getTreeNodePositionFirstRange(element);
		return range.isUndefined() ? "" : (String.valueOf(range.getStartPosition().getLine()) + "-" + String.valueOf(range.getEndPosition().getLine()));
	}
	
	public String getTreeNodeOffsetRange(Object element) {
		Range range = getTreeNodePositionFirstRange(element);
		return range.isUndefined() ? "" : (String.valueOf(range.getOffset()) + "-" + String.valueOf(range.getOffset() + range.getLength() - 1));
	}
	
	/*
	 * Other methods
	 */
	
	public abstract PositionRange getTreeNodePositionRange(Object element);
	
	public Range getTreeNodePositionFirstRange(Object element) {
		return getTreeNodePositionRange(element).getRanges().get(0);
	}
	
	/**
	 * This class is used to wrap around the root of a tree.
	 */
	public static class TreeInput {
		
		private Object root;
		
		public TreeInput(Object root) {
			this.root = root;
		}
		
		public Object getRoot() {
			return root;
		}
		
	}
	
}
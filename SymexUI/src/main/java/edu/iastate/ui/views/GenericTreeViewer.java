package edu.iastate.ui.views;

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

import edu.iastate.symex.position.Range;
import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class GenericTreeViewer extends TreeViewer {
	
	private ITreeViewer treeViewerImpl;
	
	/**
	 * Constructor
	 * @param parent
	 * @param style
	 * @param treeViewerImpl
	 */
	public GenericTreeViewer(Composite parent, int style, final ITreeViewer treeViewerImpl) {
		super(parent, style);
		
		this.treeViewerImpl = treeViewerImpl;
		
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
		setContentProvider(new MyContentProvider());
		setInput(null);
		
		/*
		 * Label provider
		 */
		TreeViewerColumn column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Tree");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return treeViewerImpl.getTreeNodeLabel(element);
			}
			
			public Image getImage(Object element) {
				return treeViewerImpl.getTreeNodeIcon(element);
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Text");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return treeViewerImpl.getTreeNodeDescription(element);
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Range range = treeViewerImpl.getTreeNodeLocation(element).getRanges().get(0);
				return range.isUndefined() ? "" : UIHelper.standardizeFilePath(range.getFilePath());
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Range range = treeViewerImpl.getTreeNodeLocation(element).getRanges().get(0);
				return range.isUndefined() ? "" : (String.valueOf(range.getStartPosition().getLine()) + "-" + String.valueOf(range.getEndPosition().getLine()));
			}
		});
		
		column = new TreeViewerColumn(this, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				Range range = treeViewerImpl.getTreeNodeLocation(element).getRanges().get(0);
				return range.isUndefined() ? "" : (String.valueOf(range.getOffset()) + "-" + String.valueOf(range.getOffset() + range.getLength() - 1));
			}
		});
	}
	
	/*
	 * Event handling
	 */
	
	public void treeNodeSelected(Object element) {
		Range range = treeViewerImpl.getTreeNodeLocation(element).getRanges().get(0);
		if (!range.isUndefined()) {
			UIHelper.selectAndReveal(range.getFile(), range.getOffset(), range.getLength());
			getControl().setFocus();
		}
	}
	
	/*
	 * Content provider
	 */
	
	private class MyContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object arg0) {
			return treeViewerImpl.getRootNodes(arg0);
		}

		@Override
		public Object[] getChildren(Object arg0) {
			return treeViewerImpl.getChildren(arg0);
		}

		@Override
		public boolean hasChildren(Object arg0) {
			return getChildren(arg0).length > 0;
		}

		@Override
		public Object getParent(Object arg0) {
			return null;
		}
		
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
		
	}
	
	/**
	 * This class is used to wrap around the root of a tree.
	 * This is a work-around for the fact that the argument to ITreeContentProvider.gelElements() must not be a node in the tree.
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
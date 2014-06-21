package edu.iastate.symex.ui.views;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.iastate.symex.position.ContinuousRegion;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.Region;
import edu.iastate.symex.run.RunSymexForFile;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.symex.constraints.AtomicConstraint;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.WriteDataModelToIfDefs;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelView extends ViewPart {

	/*
	 * DataModelView controls
	 */
	private Label filePathLabel;
	
	private Button runSymexButton;
	
	private TreeViewer dataModelTreeViewer;

	private StyledText dataModelStyledText;
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		DataModelView dataModelView = new DataModelView();
		dataModelView.createPartControl(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		dataModelTreeViewer.getControl().setFocus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		filePathLabel = new Label(parent, SWT.NONE);
		filePathLabel.setText("");
		filePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		runSymexButton = new Button(parent, SWT.PUSH);
	    runSymexButton.setText("Run Symex");
	    runSymexButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
		TabFolder tabFolder = new TabFolder(parent, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,	1));

		dataModelTreeViewer = new TreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		dataModelTreeViewer.setContentProvider(new DataModelContentProvider());
		dataModelTreeViewer.setInput(null);
		dataModelTreeViewer.getTree().setHeaderVisible(true);
		dataModelTreeViewer.getTree().setLinesVisible(true);
		//dataModelTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TreeViewerColumn column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Tree");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return getTypeOfDataNode((DataNode) element);
			}
			
			public Image getImage(Object element) {
				return getIconForDataNode((DataNode) element);
			}
		});

		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(300);
		column.getColumn().setText("Text");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTextOfDataNode((DataNode) element);
			}
		});
		
		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return UIHelper.standardizeFilePath(getFilePathOfDataNode((DataNode) element));
			}
		});
		
		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				int line = getLineInFileOfDataNode((DataNode) element);
				return (line >= 0 ? String.valueOf(line) : "");
			}
		});
		
		column = new TreeViewerColumn(dataModelTreeViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				int offset = getOffsetInFileOfDataNode((DataNode) element);
				return (offset >= 0 ? String.valueOf(offset) : "");
			}
		});
		
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Tree");
		tabItem1.setControl(dataModelTreeViewer.getControl());
		
		dataModelStyledText = new StyledText(tabFolder, SWT.BORDER);
		dataModelStyledText.setText("");
		//dataModelStyledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText("Text");
		tabItem2.setControl(dataModelStyledText);
		
	    // Event handling
	    // --------------
	    registerEventHandlers();
	}
	
	/**
	 * Registers event handlers
	 */
	private void registerEventHandlers() {
	    runSymexButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runSymexButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runSymexButtonClicked();
			}
    	});
	    
	    dataModelTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) event.getSelection()).getFirstElement(); 
				if (selectedObject != null)
					dataNodeSelected((DataNode) selectedObject);
			}
	    });
	}
	
	/**
	 * Run Symex and show results 
	 */
	private void runSymexAndShowResults(File file, File project) {
		DataModel dataModel = new RunSymexForFile(file).execute();
		
		filePathLabel.setText(file.getAbsolutePath());
		dataModelTreeViewer.setInput(dataModel);
		dataModelTreeViewer.expandToLevel(2);
		dataModelStyledText.setText(WriteDataModelToIfDefs.convert(dataModel));
	}
	
	/**
	 * Clear results
	 */
//	private void clearResults() {
//		filePathLabel.setText("");
//		dataModelTreeViewer.setInput(null);
//		dataModelStyledText.setText("");
//	}
	
	/**
	 * Invoked when the RunSymex button is clicked.
	 */
	private void runSymexButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			IProject iProject = file.getProject();
			runSymexAndShowResults(UIHelper.iFileToFile(file), UIHelper.iProjectToFile(iProject));
		}
	}
	
	/**
	 * Invoked when a DataNode is selected
	 */
	private void dataNodeSelected(DataNode dataNode) {
		ContinuousRegion region = getRegionOfDataNode(dataNode).getContinuousRegions().get(0);
		UIHelper.selectAndReveal(region.getFile(), region.getOffset(), region.getLength());
		dataModelTreeViewer.getControl().setFocus();
	}
	
	/**
	 * SelectChildNode represents a branch of a SelectNode.
	 */
	private class SelectChildNode extends DataNode {
		
		private DataNode dataNode;
		private boolean isTrueBranch;
		
		public SelectChildNode(DataNode dataNode, boolean isTrueBranch) {
			this.dataNode = dataNode;
			this.isTrueBranch = isTrueBranch;
		}
		
		public DataNode getDataNode() {
			return dataNode;
		}
		
		public boolean isTrueBranch() {
			return isTrueBranch;
		}

		@Override
		public void accept(DataModelVisitor dataModelVisitor) {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * Content provider for the Data Model tree.
	 */
	private class DataModelContentProvider implements ITreeContentProvider {
		
		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			DataModel dataModel = (DataModel) inputElement;
			DataNode outputDataNode = dataModel.getRoot();
			if (outputDataNode == null)
				outputDataNode = DataNodeFactory.createSymbolicNode();
			return new Object[]{outputDataNode};
		}

		@Override
		public Object[] getChildren(Object parentNode) {
			ArrayList<DataNode> dataNodeList = new ArrayList<DataNode>();
			
			if (parentNode instanceof ConcatNode) {
				dataNodeList.addAll(((ConcatNode) parentNode).getChildNodes());
			}
			
			else if (parentNode instanceof SelectNode) {
				SelectNode selectNode = (SelectNode) parentNode;
				if (selectNode.getNodeInTrueBranch() != null)
					dataNodeList.add(new SelectChildNode(selectNode.getNodeInTrueBranch(), true));
				if (selectNode.getNodeInFalseBranch() != null)
					dataNodeList.add(new SelectChildNode(selectNode.getNodeInFalseBranch(), false));
			}
			
			else if (parentNode instanceof SelectChildNode) {
				dataNodeList.add(((SelectChildNode) parentNode).getDataNode());
			}
			
			else if (parentNode instanceof RepeatNode) {
				dataNodeList.add(((RepeatNode) parentNode).getChildNode());
			}
			
			else if (parentNode instanceof SymbolicNode) {
				if (((SymbolicNode) parentNode).getParentNode() != null)
					dataNodeList.add(((SymbolicNode) parentNode).getParentNode());
			}
			
			else {
				// LiteralNode has no children.
			}
			
			return dataNodeList.toArray(new DataNode[]{});
		}

		@Override
		public boolean hasChildren(Object element) {
			return (getChildren(element).length > 0);
		}
		
	}
	
	/*
	 * Label provider for Data Nodes
	 */
	
	private String getTypeOfDataNode(DataNode dataNode) {
		if (dataNode instanceof SelectChildNode)
			return (((SelectChildNode) dataNode).isTrueBranch() ? "True" : "False");
		else
			return dataNode.getClass().getSimpleName().replace("Node", "");
	}
	
	private Image getIconForDataNode(DataNode dataNode) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (dataNode instanceof ConcatNode)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (dataNode instanceof SelectNode)
			imageID = ISharedImages.IMG_TOOL_CUT;
		
		else if (dataNode instanceof SelectChildNode)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else if (dataNode instanceof RepeatNode)
			imageID = ISharedImages.IMG_TOOL_REDO;
		
		else if (dataNode instanceof SymbolicNode)
			imageID = ISharedImages.IMG_OBJS_WARN_TSK;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}
	
	private String getTextOfDataNode(DataNode dataNode) {
		if (dataNode instanceof SelectNode)
			return (((SelectNode) dataNode).getConstraint() != null ? ((AtomicConstraint) ((SelectNode) dataNode).getConstraint()).getConditionString().getStringValue() : "");
		
		else if (dataNode instanceof SymbolicNode)
			return (((SymbolicNode) dataNode).getPhpNode() != null ? ((SymbolicNode) dataNode).getPhpNode().getSourceCode() : "");
		
		else if (dataNode instanceof LiteralNode)
			return UIHelper.standardizeText(((LiteralNode) dataNode).getStringValue());
		
		else
			return "";
	}
	
	private String getFilePathOfDataNode(DataNode dataNode) {
		File file = getPositionOfDataNode(dataNode).getFile();
		return (file != null ? file.getPath() : "");
	}
	
	private int getLineInFileOfDataNode(DataNode dataNode) {
		return getPositionOfDataNode(dataNode).getLine();
	}
	
	private int getOffsetInFileOfDataNode(DataNode dataNode) {
		return getPositionOfDataNode(dataNode).getOffset();
	}
	
	private Region getRegionOfDataNode(DataNode dataNode) {
		if (dataNode instanceof SelectNode)
			return (((SelectNode) dataNode).getConstraint() != null ? ((AtomicConstraint)((SelectNode) dataNode).getConstraint()).getConditionString().getRegion() : Region.UNDEFINED);
		
		else if (dataNode instanceof SymbolicNode)
			return (((SymbolicNode) dataNode).getPhpNode() != null ? ((SymbolicNode) dataNode).getPhpNode().getRegion() : Region.UNDEFINED);
		
		else if (dataNode instanceof LiteralNode)
			return ((LiteralNode) dataNode).getRegion();
		
		else
			return Region.UNDEFINED;
	}
	
	private Position getPositionOfDataNode(DataNode dataNode) {
		return getRegionOfDataNode(dataNode).getStartPosition();
	}
	
}
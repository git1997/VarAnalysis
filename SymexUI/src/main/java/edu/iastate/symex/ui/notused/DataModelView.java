package edu.iastate.symex.ui.notused;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.cmu.va.varanalysis.model.SymExModel;
import edu.cmu.va.varanalysis.model.SymExModelChangeListener;
import edu.iastate.symex.run.RunFile;
import edu.iastate.symex.ui.Activator;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 * 
 */
public class DataModelView extends ViewPart implements
		SymExModelChangeListener {

	public static final String ID = "edu.iastate.symex.ui.notused.DataModelView";

	private TreeViewer treeViewer;
	private Combo combo;
	private Text text_1;
	private Action runAction;

	private IFile currentFile = null;

	private StyledText text;

	public DataModelView() {
	}

	// /**
	// * Main method to test the user interface.
	// */
	// public static void main(String[] args) {
	// Display display = new Display();
	// Shell shell = new Shell(display);
	// shell.setSize(1000, 600);
	// DataModelView mappingView = new DataModelView();
	// mappingView.createPartControl(shell);
	// shell.open();
	// while (!shell.isDisposed()) {
	// if (!display.readAndDispatch()) {
	// display.sleep();
	// }
	// }
	// display.dispose();
	// }

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		container.setLayout(new GridLayout(2, false));
		
		runAction = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		runAction.setText("Run PhpSync");
		runAction.setToolTipText("Run PhpSync");
		runAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		getViewSite().getActionBars().getToolBarManager().add(runAction);
		
		final TabFolder tabFolder = new TabFolder(container, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));

		TabItem treeTabItem = new TabItem(tabFolder, SWT.NONE);

		treeTabItem.setText("Tree");

		// Composite treeTabCont = new Composite(tabFolder, SWT.NONE);
		tabFolder.setLayout(new GridLayout());

		{
			treeViewer = new TreeViewer(tabFolder, SWT.BORDER);
			treeTabItem.setControl(treeViewer.getControl());
			Tree tree = treeViewer.getTree();
			tree.setHeaderVisible(true);
			GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2,
					1);
			gd_tree.widthHint = 643;
			tree.setLayoutData(gd_tree);
			{
				TreeViewerColumn treeViewerColumn = new TreeViewerColumn(
						treeViewer, SWT.NONE);
				TreeColumn trclmnTree = treeViewerColumn.getColumn();
				trclmnTree.setWidth(500);
				trclmnTree.setText("Tree");
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return shorten(((MyTreeNode) element).getText());
					}
				});

				treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
				TreeColumn trclmnInfo = treeViewerColumn.getColumn();
				trclmnInfo.setWidth(300);
				trclmnInfo.setText("");
				treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {

					@Override
					public String getText(Object element) {
						return shorten(((MyTreeNode) element).getLocation());
					}
				});
			}
			treeViewer.setContentProvider(new TreeContentProvider());
			treeViewer.setInput(null);
		}

		TabItem textTabItem = new TabItem(tabFolder, SWT.NONE);

		textTabItem.setText("Text");

		text = new StyledText(tabFolder, SWT.BORDER);
		textTabItem.setControl(text);
		text.setText("foo");
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		this.setInput(Activator.getDefault().getActivePage().getActiveEditor()); /* 1 */

		IPartListener partListener = new IPartListener() {
			private IWorkbenchPart activePart;

			public void partActivated(IWorkbenchPart part) {
				if (part instanceof IEditorPart) {
					activePart = part;
					DataModelView.this.setInput((IEditorPart) part); /* 2 */
				}
			}

			public void partClosed(IWorkbenchPart part) {
				if (part == activePart) {
					activePart = null;
					DataModelView.this.setInput(null);
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partOpened(IWorkbenchPart part) {
				// TODO Auto-generated method stub

			}
		};
		Activator.getDefault().getActivePage().addPartListener(partListener); /* 3 */

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	void setInput(IEditorPart activeEditor) {
		IFile file = null;
		if (activeEditor != null) {
			IEditorInput input = activeEditor.getEditorInput();
			if (input instanceof IAdaptable) {
				file = (IFile) ((IAdaptable) input).getAdapter(IFile.class);
			}
		}
		if (file != null)
			refreshTree(SymExModel.getInstance().getDModel(file));
		else
			refreshTree(null);
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		// IToolBarManager toolbarManager = getViewSite().getActionBars()
		// .getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		// IMenuManager menuManager = getViewSite().getActionBars()
		// .getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
		// treeViewer.getControl().setFocus();
	}

	private DataNode lastModel = null;

	private void refreshTree(DataNode model) {
		if (lastModel == model)
			return;
		lastModel = model;
		
		if (model == null && !treeViewer.getControl().isDisposed()) {
			treeViewer.setInput(null);
			text.setText("");
		} else {
			treeViewer.setInput(new MyTree(model));
			text.setText(RunFile.valueToIfdefString(model, false));
		}
	}

	private void searchTreeForText(String text) {
		System.out.println("Searching for \"" + text + "\" ...");

		// for (TreeNode node : ((MyTree)
		// treeViewer.getTree().getData()).getRoot().getChildren()) {
		// searchTreeNodeForText(node, text);
		// }
	}

	private void searchTreeNodeForText(MyTreeNode node, String text) {
		if (node.getText().toLowerCase().contains(text.toLowerCase())) {
			ArrayList<MyTreeNode> path = new ArrayList<MyTreeNode>();
			MyTreeNode i = node;
			while (i != null) {
				path.add(i);
				i = i.getParent();
			}

			for (int j = path.size() - 2; j >= 1; j--) {
				System.out.print(path.get(j).getText() + " => ");
			}
			System.out.println(path.get(0).getText());
		}

		for (MyTreeNode child : node.getChildren()) {
			searchTreeNodeForText(child, text);
		}
	}

	/**
	 * Content provider for the tree.
	 */
	private class TreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return new Object[] { ((MyTree) inputElement).getRoot() };
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((MyTreeNode) parentElement).getChildren();
		}

		@Override
		public Object getParent(Object element) {
			return ((MyTreeNode) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((MyTreeNode) element).hasChildren();
		}

	}

	/*
	 * Utility methods
	 */

	/**
	 * Shortens a string
	 * 
	 * @param string
	 */
	private static String shorten(String string) {
		int beginLen = 30;
		String middleStr = "...";
		int endLen = 30;

		if (string.length() < beginLen + middleStr.length() + endLen)
			return string;
		else
			return string.substring(0, beginLen) + middleStr
					+ string.substring(string.length() - endLen);
	}

	@Override
	public void modelUpdated(IFile file) {
		if (currentFile == file)
			refreshTree(SymExModel.getInstance().getDModel(file));
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
				treeViewer.getControl().getShell(),
			"Sample View",
			message);
	}

}

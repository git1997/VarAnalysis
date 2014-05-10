package edu.iastate.varis.ui.views;

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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import scala.Option;
import scala.collection.immutable.List;
import edu.cmu.va.varanalysis.processing.FileProcessor;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.run.RunSymexForFile;

import de.fosd.typechef.conditional.Opt;
import de.fosd.typechef.parser.common.CharacterToken;
import de.fosd.typechef.parser.html.DElement;
import de.fosd.typechef.parser.html.DNode;
import de.fosd.typechef.parser.html.DText;
import de.fosd.typechef.parser.html.HAttribute;
import de.fosd.typechef.parser.html.VarDom;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.varis.core.ScalaToJava;
import edu.iastate.varis.ui.core.Varis;

/**
 * 
 * @author HUNG
 *
 */
public class VarDomView extends ViewPart {

	/*
	 * VarDomView controls
	 */
	private Label filePathLabel;
	
	private Button runVarisButton;
	
	private TreeViewer varDomTreeViewer;
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		VarDomView varDOMView = new VarDomView();
		varDOMView.createPartControl(shell);
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
		varDomTreeViewer.getControl().setFocus();
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
		
		runVarisButton = new Button(parent, SWT.TOGGLE);
	    runVarisButton.setText("Run Varis (Variability-Aware Analysis)");
	    runVarisButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		
		varDomTreeViewer = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		varDomTreeViewer.setContentProvider(new VarDomContentProvider());
		varDomTreeViewer.setInput(null);
		varDomTreeViewer.getTree().setHeaderVisible(true);
		varDomTreeViewer.getTree().setLinesVisible(true);
		varDomTreeViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TreeViewerColumn column = new TreeViewerColumn(varDomTreeViewer, SWT.NONE);
		column.getColumn().setWidth(400);
		column.getColumn().setText("Tree");
		column.setLabelProvider(new ColumnLabelProvider() {
			
			public String getText(Object element) {
				return getTextOfVarDomNode(element);
			}
			
			public Image getImage(Object element) {
				return getIconForVarDomNode(element);
			}
		});

		column = new TreeViewerColumn(varDomTreeViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Type");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return getTypeOfVarDomNode(element);
			}
		});
		
		column = new TreeViewerColumn(varDomTreeViewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("File");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return UIHelper.standardizeFilePath(getFilePathOfVarDomNode(element));
			}
		});
		
		column = new TreeViewerColumn(varDomTreeViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Line");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				int line = getLineInFileOfVarDomNode(element);
				return (line >= 0 ? String.valueOf(line) : "");
			}
		});
		
		column = new TreeViewerColumn(varDomTreeViewer, SWT.NONE);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Offset");
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				int offset = getOffsetInFileOfVarDomNode(element);
				return (offset >= 0 ? String.valueOf(offset) : "");
			}
		});
	    
	    // Event handling
	    // --------------
	    registerEventHandlers();
	}
	
	/**
	 * Registers event handlers
	 */
	private void registerEventHandlers() {
	    runVarisButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runVarisButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runVarisButtonClicked();
			}
    	});
	    
	    varDomTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedObject = ((IStructuredSelection) event.getSelection()).getFirstElement(); 
				if (selectedObject != null)
					varDomNodeSelected(selectedObject);
			}
	    });
	}
	
	/**
	 * Run Varis and show results 
	 */
	private void runVarisAndShowResults(File file, File project) {
		DataModel dataModel = new RunSymexForFile(file).execute();
		VarDom varDom = new FileProcessor().parseVarDom(dataModel.getRoot(), null);
		
		filePathLabel.setText(file.getAbsolutePath());
		varDomTreeViewer.setInput(varDom);
		varDomTreeViewer.expandAll();
	}
		
	/**
	 * Clear results
	 */
	private void clearResults() {
		filePathLabel.setText("");
		varDomTreeViewer.setInput(null);
	}
	
	/**
	 * Invoked when the RunVaris button is clicked.
	 */
	private void runVarisButtonClicked() {
		if (runVarisButton.getSelection()) {
			UIHelper.saveAllEditors();
			if (UIHelper.getActiveEditor() != null) {
				IFile file = UIHelper.getActiveEditorFile();
				IProject iProject = file.getProject();
				runVarisAndShowResults(UIHelper.iFileToFile(file), UIHelper.iProjectToFile(iProject));
			}
			
			Varis.enableVaris();
		}
		else {
			clearResults();
			Varis.disableVaris();
		}
	}
	
	/**
	 * Invoked when a VarDom node is selected
	 */
	private void varDomNodeSelected(Object element) {
		UIHelper.selectAndReveal(getFileOfVarDomNode(element), getOffsetInFileOfVarDomNode(element), getSelectionLengthOfVarDomNode(element));
		varDomTreeViewer.getControl().setFocus();
	}
	
	/**
	 * Content provider for the VarDom tree
	 */
	private class VarDomContentProvider implements ITreeContentProvider {
		
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
			VarDom varDom = (VarDom) inputElement;
			ArrayList<Opt<DElement>> children = ScalaToJava.scalaListToJavaList(ScalaToJava.getChildrenOfVarDom(varDom));
			
			ArrayList<Object> list = new ArrayList<Object>();
			for (Opt<DElement> element : children) {
				if (element.feature().isTautology())
					list.add(element.entry());
				else
					list.add(element);
			}
			
			return list.toArray(new Object[]{});
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object[] getChildren(Object parentNode) {
			ArrayList<Object> childNodes = new ArrayList<Object>();
			
			if (parentNode instanceof Opt<?>) {
				childNodes.add(((Opt<DElement>) parentNode).entry());
			}
			
			else if (parentNode instanceof DNode) {
				for (Opt<DElement> element : ScalaToJava.scalaListToJavaList(((DNode) parentNode).children())) {
					if (element.feature().isTautology())
						childNodes.add(element.entry());
					else
						childNodes.add(element);
				}
			}
			
			else if (parentNode instanceof DText) {
				// No children
			}
			
			else {
				// Should not reach here.
			}
			
			return childNodes.toArray(new Object[]{});
		}

		@Override
		public boolean hasChildren(Object element) {
			return (getChildren(element).length > 0);
		}
		
	}
	
	/*
	 * Label provider for Data Nodes
	 */
	
	private String getTypeOfVarDomNode(Object element) {
		if (element instanceof DNode)
			return "HTML Element";
		else if (element instanceof DText)
			return "HTML Text";
		else if (element instanceof Opt)
			return "Condition";
		else
			return "";
	}
	
	private Image getIconForVarDomNode(Object element) {
		// http://shinych.blogspot.com/2007/05/eclipse-shared-images.html
		String imageID;
		
		if (element instanceof DNode)
			imageID = ISharedImages.IMG_OBJ_FOLDER;
		
		else if (element instanceof Opt)
			imageID = ISharedImages.IMG_TOOL_FORWARD;
		
		else
			imageID = ISharedImages.IMG_OBJ_FILE;
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageID);
	}
	
	private String getTextOfVarDomNode(Object element) {
		if (element instanceof Opt)
			return ((Opt<?>) element).feature().toString();
		
		else if (element instanceof DNode) {
			ArrayList<Opt<HAttribute>> attrs = ScalaToJava.scalaListToJavaList3(((DNode) element).attributes());
			StringBuilder str = new StringBuilder();
			for (Opt<HAttribute> attr : attrs) {
				Option<String> a = attr.entry().value();
				str.append(" " + attr.entry().name().name() + "=" + ScalaToJava.getOptionValue(a));
			}
			
			return "<" + ((DNode) element).name().name() + str.toString() + ">";
		}
		
		else if (element instanceof DText) {
			List<Opt<CharacterToken>> scalaList = ((DText) element).value();
			ArrayList<Opt<CharacterToken>> javaList = ScalaToJava.scalaListToJavaList2(scalaList);
			StringBuilder str = new StringBuilder();
			for (Opt<CharacterToken> token: javaList) {
				str.append(token.entry().getText());
			}
			return UIHelper.standardizeText(str.toString());
		}
		
		else
			return "?";
	}
	
	private Position getPositionOfVarDomNode(Object element) {
		if (element instanceof DNode) {
			String file = ScalaToJava.getFileOfDString(((DNode) element).name());
			int position = ScalaToJava.getPositionOfDString(((DNode) element).name());
			return new Position(new File(file), position);
		}
		
		else if (element instanceof DText) {
			List<Opt<CharacterToken>> scalaList = ((DText) element).value();
			ArrayList<Opt<CharacterToken>> javaList = ScalaToJava.scalaListToJavaList2(scalaList);
			
			String file = javaList.get(0).entry().getPosition().getFile();
			int position = javaList.get(0).entry().getPosition().getColumn();
			return new Position(new File(file), position);
		}
		
		else
			return Position.UNDEFINED;
	}
	
	private File getFileOfVarDomNode(Object element) {
		return getPositionOfVarDomNode(element).getFile();
	}
	
	private String getFilePathOfVarDomNode(Object element) {
		File file = getPositionOfVarDomNode(element).getFile();
		return (file != null ? file.getPath() : "");
	}
	
	private int getOffsetInFileOfVarDomNode(Object element) {
		return getPositionOfVarDomNode(element).getOffset();
	}
	
	private int getLineInFileOfVarDomNode(Object element) {
		return getPositionOfVarDomNode(element).getLine();
	}
	
	private int getSelectionLengthOfVarDomNode(Object element) {
		if (element instanceof DNode) {
			return ((DNode) element).name().name().length();
		}
		
		else if (element instanceof DText) {
			List<Opt<CharacterToken>> scalaList = ((DText) element).value();
			ArrayList<Opt<CharacterToken>> javaList = ScalaToJava.scalaListToJavaList2(scalaList);
			
			int firstTokenPosition = javaList.get(0).entry().getPosition().getColumn();
			int lastTokenPosition = javaList.get(javaList.size() - 1).entry().getPosition().getColumn();
			
			return lastTokenPosition - firstTokenPosition + 1; 
		}
		
		else
			return 1;
	}
	
}
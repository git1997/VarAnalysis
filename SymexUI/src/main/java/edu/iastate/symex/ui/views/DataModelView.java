package edu.iastate.symex.ui.views;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.iastate.symex.ui.UIHelper;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;

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

		dataModelTreeViewer = new DataModelTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Tree");
		tabItem1.setControl(dataModelTreeViewer.getControl());
		
		dataModelStyledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
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
	}
	
	/**
	 * Invoked when the RunSymex button is clicked.
	 */
	private void runSymexButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runSymexAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Run Symex and show results 
	 */
	private void runSymexAndShowResults(File file) {
		DataModel dataModel = new PhpExecuter().execute(file);
		
		filePathLabel.setText(file.getAbsolutePath());
		dataModelTreeViewer.setInput(dataModel);
		dataModelTreeViewer.expandToLevel(2);
		dataModelStyledText.setText(dataModel.toIfdefString());
	}
	
}
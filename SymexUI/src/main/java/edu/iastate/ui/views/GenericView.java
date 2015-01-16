package edu.iastate.ui.views;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.iastate.symex.ui.UIHelper;

/**
 * 
 * @author HUNG
 *
 */
public class GenericView extends ViewPart {

	/*
	 * View controls
	 */
	protected Label fileLabel;
	
	protected int numButtons;
	protected ArrayList<Button> buttons = new ArrayList<Button>();

	protected TabFolder tabFolder;
	
	protected int numTabControls;
	protected ArrayList<Control> tabControls = new ArrayList<Control>();
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		GenericView debuggerView = new GenericView(1, 2);
		debuggerView.createPartControl(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	/**
	 * Constructor
	 * @param numButtons
	 * @param numTabControls
	 */
	public GenericView(int numButtons, int numTabControls) {
		this.numButtons = numButtons;
		this.numTabControls = numTabControls;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		if (!tabControls.isEmpty())
			tabControls.get(0).setFocus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1 + numButtons, false));
		
		fileLabel = new Label(parent, SWT.NONE);
		fileLabel.setText("");
		fileLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		for (int i = 0; i < numButtons; i++) {
			Button button = createButton(parent, i);
			button.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			
			buttons.add(button);
		}
	    
		tabFolder = new TabFolder(parent, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1 + numButtons, 1));

		for (int i = 0; i < numTabControls; i++) {
			TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			Control tabControl = createTabControl(tabFolder, tabItem, i);
			tabItem.setControl(tabControl);
			
			tabControls.add(tabControl);
		}
		
	    // Event handling
	    // --------------
	    registerEventHandlers();
	}
	
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Button " + buttonNumber);
		return button;
	}
	
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		tabItem.setText("Tab Item " + tabNumber);
		StyledText styledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setText("Some text " + tabNumber);
		return styledText;
	}
	
	/**
	 * Registers event handlers
	 */
	private void registerEventHandlers() {
		for (Button button : buttons) {
			final int buttonNumber = buttons.indexOf(button);
			button.addSelectionListener(new SelectionListener() {
	    	
				@Override
				public void widgetSelected(SelectionEvent event) {
					buttonClicked(buttonNumber);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					buttonClicked(buttonNumber);
				}
			});
		}
	}
	
	/**
	 * Invoked when a button is clicked.
	 */
	private void buttonClicked(int buttonNumber) {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			File file = UIHelper.iFileToFile(UIHelper.getActiveEditorFile());
			fileLabel.setText(file.getAbsolutePath());
			buttonClicked(file, buttonNumber);
		}
	}
	
	/**
	 * Invoked when a button is clicked.
	 */
	public void buttonClicked(File file, int buttonNumber) {
	}

}
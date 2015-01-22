package edu.iastate.symex.ui.views;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import edu.iastate.symex.debug.DebugInfo;
import edu.iastate.symex.debug.Debugger;
import edu.iastate.ui.views.GenericTreeViewer;
import edu.iastate.ui.views.GenericView;

/**
 * 
 * @author HUNG
 *
 */
public class DebuggerView extends GenericView {

	private GenericTreeViewer dataModelTreeViewer, debuggerTreeViewer;
	private StyledText styledText;
	
	/**
	 * Constructor
	 */
	public DebuggerView() {
		super(1, 3);
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Debug Symex");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		if (tabNumber == 0) {
			tabItem.setText("DataModel");
			dataModelTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new DataModelTreeViewer());
			return dataModelTreeViewer.getControl();
		}
		else if (tabNumber == 1) {
			tabItem.setText("Trace");
			debuggerTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new DebuggerTreeViewer());
			return debuggerTreeViewer.getControl();
		}
		else {
			tabItem.setText("DataModel (Text)");
			styledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			return styledText;
		}
	}
	
	@Override
	public void buttonClicked(File file, int buttonNumber) {
		DebugInfo debugInfo = new Debugger().debug(file);
		
		dataModelTreeViewer.setInput(debugInfo.getDataModel());
		dataModelTreeViewer.expandToLevel(2);
		debuggerTreeViewer.setInput(debugInfo.getTrace());
		debuggerTreeViewer.expandToLevel(1);
		styledText.setText(debugInfo.getDataModel().toIfdefString());
		//styledText.setText(debugInfo.getTrace().printTraceToString());
	}
	
}
package edu.iastate.symex.ui.views;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.ui.views.GenericTreeViewer;
import edu.iastate.ui.views.GenericView;

/**
 * 
 * @author HUNG
 *
 */
public class DataModelView extends GenericView {
	
	private GenericTreeViewer treeViewer;
	private StyledText styledText;
	
	/**
	 * Constructor
	 */
	public DataModelView() {
		super(1, 2);
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Run Symex");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		if (tabNumber == 0) {
			tabItem.setText("Tree");
			treeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new DataModelTreeViewer());
			return treeViewer.getControl();
		}
		else {
			tabItem.setText("Text");
			styledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			return styledText;
		}
	}
	
	@Override
	public void buttonClicked(File file, int buttonNumber) {
		DataModel dataModel = new PhpExecuter().execute(file);
		
		treeViewer.setInput(dataModel);
		treeViewer.expandToLevel(2);
		styledText.setText(dataModel.toIfdefString());
	}
	
}
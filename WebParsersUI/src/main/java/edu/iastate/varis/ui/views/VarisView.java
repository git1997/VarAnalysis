package edu.iastate.varis.ui.views;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;

import edu.iastate.parsers.html.core.PhpExecuterAndParser;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.ui.UIHelper;
import edu.iastate.symex.util.FileIO;
import edu.iastate.ui.views.GenericView;
import edu.iastate.varis.ui.core.VarisManager;

/**
 * 
 * @author HUNG
 *
 */
public class VarisView extends GenericView {
	
	private static String ENTRIES_FILE_NAME = "__ENTRIES__.txt";
	
	private StyledText statusStyledText;

	/**
	 * Constructor
	 */
	public VarisView() {
		super(1, 1);
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.TOGGLE);
		if (buttonNumber == 0)
			button.setText("Enable Varis");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		tabItem.setText("Status");
		statusStyledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		return statusStyledText;
	}
	
	@Override
	public void buttonClicked(File file, int buttonNumber) {
		if (buttonNumber == 0) {
			if (buttons.get(buttonNumber).getSelection()) {
				ArrayList<File> entries = getEntries();
				statusStyledText.append("Found " + entries.size() + " entries in file " + ENTRIES_FILE_NAME + System.lineSeparator());
				
				if (entries.isEmpty()) {
					statusStyledText.append("Using current file as entry" + System.lineSeparator());
					entries.add(UIHelper.iFileToFile(UIHelper.getActiveEditorFile()));
				}
				
				for (File entry : entries) {
					statusStyledText.append("Executing " + entry.getAbsolutePath() + "..." + System.lineSeparator());
					HtmlDocument htmlDocument = new PhpExecuterAndParser().executeAndParse(entry);
					VarisManager.getInstance().addHtmlDocument(htmlDocument);
				}
				VarisManager.getInstance().enable();
				
				statusStyledText.append("Done.");
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(UIHelper.getActiveEditor());
			}
			else {
				VarisManager.getInstance().removeHtmlDocuments();
				VarisManager.getInstance().disable();
				
				statusStyledText.setText("");
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(UIHelper.getActiveEditor());
			}
		}
	}
	
	private ArrayList<File> getEntries() {
		ArrayList<File> entries = new ArrayList<File>();
		
		File entriesFile = new File(UIHelper.getProjectPath(), ENTRIES_FILE_NAME);
		if (!entriesFile.exists())
			return entries;
		
		String entriesFileContent = FileIO.readStringFromFile(entriesFile);
		for (String entry : entriesFileContent.split("\n")) {
			entry = entry.trim();
			if (!entry.isEmpty())
				entries.add(new File(UIHelper.getProjectPath(), entry));
		}
		
		return entries;
	}
	
}
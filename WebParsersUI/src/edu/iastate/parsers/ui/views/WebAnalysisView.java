package edu.iastate.parsers.ui.views;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.Program;
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
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;

import edu.iastate.analysis.references.detection.FindReferencesInFile;
import edu.iastate.analysis.references.detection.ShowStatisticsOnReferences;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.symex.ui.views.GenericTreeViewer;
import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class WebAnalysisView extends ViewPart {

	/*
	 * WebAnalysisView controls
	 */
	private Label filePathLabel;
	
	private Button runAnalysisButton, runPhpAstButton, runJsAstButton;
	
	private TabFolder tabFolder;
	
	private TreeViewer analysisResultTreeViewer, phpAstTreeViewer, jsAstTreeViewer;

	private StyledText statsStyledText;
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		WebAnalysisView dataModelView = new WebAnalysisView();
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
		analysisResultTreeViewer.getControl().setFocus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));
		
		filePathLabel = new Label(parent, SWT.NONE);
		filePathLabel.setText("");
		filePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		runAnalysisButton = new Button(parent, SWT.PUSH);
	    runAnalysisButton.setText("Run Analysis");
	    runAnalysisButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
	    runPhpAstButton = new Button(parent, SWT.PUSH);
	    runPhpAstButton.setText("PHP AST");
	    runPhpAstButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
	    runJsAstButton = new Button(parent, SWT.PUSH);
	    runJsAstButton.setText("JavaScript AST");
	    runJsAstButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
		tabFolder = new TabFolder(parent, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4,	1));

		analysisResultTreeViewer = new AnalysisResultTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		phpAstTreeViewer = new PhpAstTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		jsAstTreeViewer = new JsAstTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Analysis Result");
		tabItem1.setControl(analysisResultTreeViewer.getControl());
		
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText("PHP AST");
		tabItem2.setControl(phpAstTreeViewer.getControl());
		
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NONE);
		tabItem3.setText("JavaScript AST");
		tabItem3.setControl(jsAstTreeViewer.getControl());
		
		statsStyledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL) ;
		statsStyledText.setText("");
		//statsStyledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		TabItem tabItem4 = new TabItem(tabFolder, SWT.NONE);
		tabItem4.setText("Analysis Stats");
		tabItem4.setControl(statsStyledText);
		
	    // Event handling
	    // --------------
	    registerEventHandlers();
	}
	
	/**
	 * Registers event handlers
	 */
	private void registerEventHandlers() {
	    runAnalysisButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runAnalysisButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runAnalysisButtonClicked();
			}
    	});
	    
	    runPhpAstButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runPhpAstButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runPhpAstButtonClicked();
			}
    	});
	    
	    runJsAstButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runJsAstButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runJsAstButtonClicked();
			}
    	});
	}
	
	/**
	 * Invoked when the RunAnalysis button is clicked.
	 */
	private void runAnalysisButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runAnalysisAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Invoked when the RunPhpAst button is clicked.
	 */
	private void runPhpAstButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runPhpAstAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Invoked when the RunJsAst button is clicked.
	 */
	private void runJsAstButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runJsAstAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Run Analysis and show results 
	 */
	private void runAnalysisAndShowResults(File file) {
		ReferenceManager referenceManager = new FindReferencesInFile(file).execute();
		
		filePathLabel.setText(file.getAbsolutePath());
		analysisResultTreeViewer.setInput(referenceManager);
		analysisResultTreeViewer.expandToLevel(1);
		tabFolder.setSelection(0);
		statsStyledText.setText(new ShowStatisticsOnReferences().showStatistics(referenceManager));
	}
	
	/**
	 * Run PhpAst and show results 
	 */
	private void runPhpAstAndShowResults(File file) {
		try {
			org.eclipse.php.internal.core.ast.nodes.ASTParser parser = org.eclipse.php.internal.core.ast.nodes.ASTParser.newParser(PHPVersion.PHP5, true);
			char[] source = FileIO.readStringFromFile(file).toCharArray();
			parser.setSource(source);
			Program program = parser.createAST(null);
			
			filePathLabel.setText(file.getAbsolutePath());
			phpAstTreeViewer.setInput(new GenericTreeViewer.TreeInput(program));
			phpAstTreeViewer.expandToLevel(2);
			tabFolder.setSelection(1);
		} catch (Exception e) {
			System.out.println("In WebAnalysis.java: Error parsing " + file + " (" + e.getMessage() + ")");
		}
	}
	
	/**
	 * Run JsAst and show results 
	 */
	private void runJsAstAndShowResults(File file) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		char[] source = FileIO.readStringFromFile(file).toCharArray();
        parser.setSource(source);
        ASTNode rootNode = parser.createAST(null);
        
		filePathLabel.setText(file.getAbsolutePath());
		jsAstTreeViewer.setInput(new GenericTreeViewer.TreeInput(rootNode));
		jsAstTreeViewer.expandToLevel(2);
		tabFolder.setSelection(2);
	}
	
}
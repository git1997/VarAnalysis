package edu.iastate.parsers.ui.views;

import java.io.File;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;

import edu.iastate.analysis.references.detection.ReferenceDetector;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.analysis.references.Reference;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.util.FileIO;
import edu.iastate.ui.views.GenericTreeViewer;
import edu.iastate.ui.views.GenericView;
import edu.iastate.webslice.core.ShowStatisticsOnReferences;

/**
 * 
 * @author HUNG
 *
 */
public class WebAnalysisView extends GenericView {
	
	public static WebAnalysisView inst = null;

	private GenericTreeViewer forwardSliceTreeViewer, backwardSliceTreeViewer, phpAstTreeViewer, jsAstTreeViewer; 

	private StyledText statsStyledText;
	
	/**
	 * Constructor
	 */
	public WebAnalysisView() {
		super(3, 5);
		WebAnalysisView.inst = this;
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		if (buttonNumber == 0)
			button.setText("Run Analysis");
		else if (buttonNumber == 1)
			button.setText("PHP AST");
		else if (buttonNumber == 2)
			button.setText("JavaScript AST");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		if (tabNumber == 0) {
			tabItem.setText("Forward Slicing");
			forwardSliceTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new AnalysisResultTreeViewer(true));
			forwardSliceTreeViewer.getTree().getColumn(0).setText("Slice");
			forwardSliceTreeViewer.getTree().getColumn(1).setText("Data Entity and Constraint");
			return forwardSliceTreeViewer.getControl();
		}
		else if (tabNumber == 1) {
			tabItem.setText("Backward Slicing");
			backwardSliceTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new AnalysisResultTreeViewer(false));
			backwardSliceTreeViewer.getTree().getColumn(0).setText("Slice");
			backwardSliceTreeViewer.getTree().getColumn(1).setText("Data Entity and Constraint");
			return backwardSliceTreeViewer.getControl();
		}
		else if (tabNumber == 2) {
			tabItem.setText("PHP AST");
			phpAstTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new PhpAstTreeViewer());
			return phpAstTreeViewer.getControl();
		}
		else if (tabNumber == 3) {
			tabItem.setText("JavaScript AST");
			jsAstTreeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new JsAstTreeViewer());
			return jsAstTreeViewer.getControl();
		}
		else {
			tabItem.setText("Analysis Stats");
			statsStyledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			return statsStyledText;
		}
	}
	
	@Override
	public void buttonClicked(File file, int buttonNumber) {
		if (buttonNumber == 0) {
			ReferenceManager referenceManager = new ReferenceDetector().detect(file);
		
			forwardSliceTreeViewer.setInput(referenceManager);
			forwardSliceTreeViewer.expandToLevel(1);
			
			backwardSliceTreeViewer.setInput(referenceManager);
			backwardSliceTreeViewer.expandToLevel(1);
			
			statsStyledText.setText(new ShowStatisticsOnReferences().showStatistics(referenceManager));
			tabFolder.setSelection(1);
		}
		else if (buttonNumber == 1) {
			try {
				org.eclipse.php.internal.core.ast.nodes.ASTParser parser = org.eclipse.php.internal.core.ast.nodes.ASTParser.newParser(PHPVersion.PHP5, true);
				char[] source = FileIO.readStringFromFile(file).toCharArray();
				parser.setSource(source);
				Program program = parser.createAST(null);
				
				phpAstTreeViewer.setInput(new GenericTreeViewer.TreeInput(program));
				phpAstTreeViewer.expandToLevel(2);
				tabFolder.setSelection(2);
			} catch (Exception e) {
				System.out.println("In WebAnalysisView.java: Error parsing " + file + " (" + e.getMessage() + ")");
			}
		}
		else {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			char[] source = FileIO.readStringFromFile(file).toCharArray();
			parser.setSource(source);
			ASTNode rootNode = parser.createAST(null);
        
			jsAstTreeViewer.setInput(new GenericTreeViewer.TreeInput(rootNode));
			jsAstTreeViewer.expandToLevel(2);
			tabFolder.setSelection(3);
		}
	}
	
	/**
	 * Invoked when the user wants to display the forward slice for a given position
	 */
	public void displayForwardSlice(Position position) {
		displaySlice(forwardSliceTreeViewer, position);
		tabFolder.setSelection(0);
	}
	
	/**
	 * Invoked when the user wants to display the backward slice for a given position
	 */
	public void displayBackwardSlice(Position position) {
		displaySlice(backwardSliceTreeViewer, position);
		tabFolder.setSelection(1);
	}
	
	private void displaySlice(TreeViewer sliceTreeViewer, Position position) {
		for (TreeItem treeItem : sliceTreeViewer.getTree().getItems()) {
			Reference reference = (Reference) treeItem.getData();
			if (reference.getLocation().getStartPosition().getFile().equals(position.getFile())
					&& reference.getLocation().getStartPosition().getOffset() <= position.getOffset()
					&& position.getOffset() < reference.getLocation().getEndPosition().getOffset()) {
				
				sliceTreeViewer.getTree().select(treeItem);
				sliceTreeViewer.getTree().setFocus();
			}
		}
	}
	
}
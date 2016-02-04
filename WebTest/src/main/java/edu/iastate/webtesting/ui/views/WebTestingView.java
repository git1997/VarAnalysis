package edu.iastate.webtesting.ui.views;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.ReadWriteDataModelToFromXml;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.ui.views.GenericTreeViewer;
import edu.iastate.ui.views.GenericView;
import edu.iastate.webtesting.outputcoverage.ComputeOutputCoverage;
import edu.iastate.webtesting.outputcoverage.ComputeOutputCoverageTest;
import edu.iastate.webtesting.outputcoverage.OutputCoverage;
import edu.iastate.webtesting.outputcoverage.DataModelCoverage;
import edu.iastate.webtesting.util_clone.Config;
import edu.iastate.webtesting.util_clone.XmlReadWrite;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public class WebTestingView extends GenericView {
	
	private GenericTreeViewer treeViewer;
	private StyledText cModelFiles;
	private StyledText debugInfo;
	
	/**
	 * Constructor
	 */
	public WebTestingView() {
		super(1, 3);
	}
	
	@Override
	public Button createButton(Composite parent, int buttonNumber) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Compute Output Coverage");
		return button;
	}
	
	@Override
	public Control createTabControl(TabFolder tabFolder, TabItem tabItem, int tabNumber) {
		if (tabNumber == 0) {
			tabItem.setText("Data Model Coverage");
			treeViewer = new GenericTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION, new WebTestingTreeViewer());
			return treeViewer.getControl();
		}
		else if (tabNumber == 1) {
			tabItem.setText("CModel File(s)");
			cModelFiles = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			cModelFiles.setText(ComputeOutputCoverageTest.CMODEL_XML_FILE);
			return cModelFiles;
		}
		else {
			tabItem.setText("Debug Info");
			debugInfo = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			return debugInfo;
		}
	}
	
	@Override
	public void buttonClicked(File file1, int buttonNumber) {
		DataModel dataModel = new ReadWriteDataModelToFromXml().readDataModelFromXmlFile(ComputeOutputCoverageTest.DATA_MODEL_FILE);
		DataModelCoverage dataModelCoverage = new DataModelCoverage(dataModel, new HashSet<LiteralNode>());
		StringBuilder debugText = new StringBuilder();
		
		for (String cModelFile : getCModelFiles()) {
			CondValue cModel = new XmlReadWrite().readCondValueFromXmlFile(new File(cModelFile));
			OutputCoverage outputCoverage = new ComputeOutputCoverage().compute(cModel, dataModel, null);
			
			dataModelCoverage.addCoverage(outputCoverage.getDataModelCoverage());
			
			debugText.append("Running " + cModelFile + System.lineSeparator());
			debugText.append(outputCoverage.toDebugString());
		}
		debugText.append("========== Overall DataModel Coverage ==========" + System.lineSeparator());
		debugText.append(dataModelCoverage.toDebugString());
		
		treeViewer.setInput(dataModelCoverage);
		treeViewer.expandToLevel(2);
		debugInfo.setText(debugText.toString());
	}
	
	private List<String> getCModelFiles() {
		String text = cModelFiles.getText();
		List<String> files = new ArrayList<String>();
		if (text.equals("All")) {
			for (File file : new File(ComputeOutputCoverageTest.CMODEL_XML_FILE).getParentFile().getParentFile().listFiles()) {
				if (!file.getName().startsWith("."))
					files.add(file.getAbsolutePath() + "/" + Config.CMODEL_XML_FILE);
			}
		}
		else {
			for (String file : text.split("\r?\n")) {
				files.add(file);
			}
		}
		return files;
	}
}
package edu.iastate.webtesting.visualization;

import java.util.HashMap;
import java.util.Map;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.parsers.html.htmlparser.DataModelParser;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel_modified.ReadWriteDataModelCoverageToFromXml;
import edu.iastate.symex.util.FileIO;
import edu.iastate.webtesting.outputcoverage.DataModelCoverage;

/**
 * 
 * @author HUNG
 *
 */
public class WriteHtmlCoverageToHtml extends HtmlDocumentVisitor {
	
	public static final String DATA_MODEL_COVERAGE_FILE = WriteHtmlDocumentToHtml.WORKSPACE + "/input/data-model-coverage.xml";
	public static final String OUTPUT_FILE = WriteHtmlDocumentToHtml.WORKSPACE + "/output-with-coverage.html";
	
	/**
	 * Main entry
	 */
	public static void main(String[] args) {
		DataModelCoverage dataModelCoverage = new ReadWriteDataModelCoverageToFromXml().readDataModelCoverageFromXmlFile(DATA_MODEL_COVERAGE_FILE);
		DataModel dataModel = dataModelCoverage.getDataModel();
		final Map<DataNode, Float> coverageMap = dataModelCoverage.getCoverageMap();
		
		// Map the true branch and false branch of a SelectNode with their coverages
		final Map<Constraint, Float> trueBranchCoverage = new HashMap<Constraint, Float>();
		final Map<Constraint, Float> falseBranchCoverage = new HashMap<Constraint, Float>();
		
		dataModel.getRoot().accept(new DataModelVisitor() {
			
			public boolean visitSelectNode(SelectNode selectNode) {
				Constraint constraint = selectNode.getConstraint();
				DataNode trueChild = selectNode.getNodeInTrueBranch();
				DataNode falseChild = selectNode.getNodeInFalseBranch();
				if (coverageMap.containsKey(trueChild))
					updateCoverage(trueBranchCoverage, constraint, coverageMap.get(trueChild));
				else 
					updateCoverage(trueBranchCoverage, constraint, (float) 0);
				if (coverageMap.containsKey(falseChild))
					updateCoverage(falseBranchCoverage, constraint, coverageMap.get(falseChild));
				else
					updateCoverage(falseBranchCoverage, constraint, (float) 0);
				return true;
			}
		});
		
		HtmlDocument htmlDocument = new DataModelParser().parse(dataModel);
		WriteHtmlDocumentToHtml writeHtmlDocumentToHtml = new WriteHtmlDocumentToHtml();
		writeHtmlDocumentToHtml.setBranchCoverages(trueBranchCoverage, falseBranchCoverage);
		String html = writeHtmlDocumentToHtml.convertToHtml(htmlDocument);
		FileIO.writeStringToFile(html, OUTPUT_FILE);
	}
	
	private static void updateCoverage(Map<Constraint, Float> branchCoverage, Constraint constraint, Float coverage) {
		if (branchCoverage.containsKey(constraint))
			branchCoverage.put(constraint, (float) -2); // -2 means the coverage is ambiguous (two different coverage values mapped to the same constraint)
		else
			branchCoverage.put(constraint, coverage);
	}
}
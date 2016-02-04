package edu.iastate.webtesting.visualization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.ReadWriteDataModelToFromXml;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel_modified.ReadWriteDataModelCoverageToFromXml;
import edu.iastate.webtesting.outputcoverage.ComputeOutputCoverage;
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
public class WriteHtmlCoverageToXml extends HtmlDocumentVisitor {
	
	public static final String DATA_MODEL_FILE = "/Users/HUNG/Desktop/Web Testing Project/Data/SchoolMate-1.5.4/data-model.xml";
	public static final String TEST_CASE_FOLDER = "/Users/HUNG/Desktop/Web Testing Project/Data/SchoolMate-1.5.4/Test Cases";
	public static final String DATA_MODEL_COVERAGE_FILE = "/Users/HUNG/Desktop/data-model-coverage.xml";
	
	public static void main(String[] args) {
		DataModel dataModel = new ReadWriteDataModelToFromXml().readDataModelFromXmlFile(DATA_MODEL_FILE);
		DataModelCoverage dataModelCoverage = new DataModelCoverage(dataModel, new HashSet<LiteralNode>());
		for (String cModelFile : getCModelFiles()) {
			CondValue cModel = new XmlReadWrite().readCondValueFromXmlFile(new File(cModelFile));
			OutputCoverage outputCoverage = new ComputeOutputCoverage().compute(cModel, dataModel, null);
			dataModelCoverage.addCoverage(outputCoverage.getDataModelCoverage());
		}
		new ReadWriteDataModelCoverageToFromXml().writeDataModelCoverageToXmlFile(dataModelCoverage, DATA_MODEL_COVERAGE_FILE);
	}
	
	private static List<String> getCModelFiles() {
		List<String> files = new ArrayList<String>();
		for (File file : new File(TEST_CASE_FOLDER).listFiles()) {
			if (!file.getName().startsWith("."))
				files.add(file.getAbsolutePath() + "/" + Config.CMODEL_XML_FILE);
		}
		return files;
	}
}
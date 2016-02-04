package edu.iastate.webtesting.outputcoverage;

import java.io.File;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.ReadWriteDataModelToFromXml;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.webtesting.util_clone.XmlReadWrite;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeOutputCoverageTest {
	public static final String DATA_MODEL_FILE = "/Users/HUNG/Desktop/Web Testing Project/Data/SchoolMate-1.5.4/data-model.xml";
	public static final String CMODEL_XML_FILE = "/Users/HUNG/Desktop/Web Testing Project/Data/SchoolMate-1.5.4/Test Cases/[14] index.php?delete[]=45&deletesemester=&selectsemester=&page2=7&onpage=1&logout=&page=1/cmodel.xml";
	 
	public static void main(String[] args) {
		CondValue cModel = new XmlReadWrite().readCondValueFromXmlFile(new File(CMODEL_XML_FILE));
		DataModel dataModel = new ReadWriteDataModelToFromXml().readDataModelFromXmlFile(DATA_MODEL_FILE);
		OutputCoverage outputCoverage = new ComputeOutputCoverage().compute(cModel, dataModel, null);
		MyLogger.log(outputCoverage.toDebugString());
	}
}
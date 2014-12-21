package edu.iastate.analysis.references.detection;

import java.io.File;

import edu.iastate.parsers.html.core.ParseDataModel;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class FindReferencesInFile {
	
	public static String PHP_FILE = //"/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/SchoolMate-1.5.4/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/addressbookv6.2.12/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/TimeClock-1.04/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/UPB-2.2.7/admin_forums.php";
									//"/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/testWebSlice.php";
									"/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/testCallingContext.php";
	
	private File phpFile;
	
	/**
	 * The entry point of the program.
	 */
	public static void main(String[] args) {
		new FindReferencesInFile(new File(PHP_FILE)).execute();
	}
	
	/**
	 * Constructor.
	 */
	public FindReferencesInFile(File phpFile) {
		this.phpFile = phpFile;
	}
	
	/**
	 * Executes the file.
	 */
	public ReferenceManager execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Started.");
		
		// Step 1: Create the data model & find PHP/SQL references in the PHP code
		ReferenceManager referenceManager = new ReferenceManager();
		DataModel dataModel = createDataModelAndFindPhpSqlReferences(referenceManager);
		
		// Step 2: Parse the DataModel
		HtmlDocument htmlDocument = parseDataModel(dataModel);
		
		// Step 3: Find HTML/JavaScript references in the HTML document
		findReferencesInHtmlDocument(htmlDocument, referenceManager);
		
		// Step 4: Resolve data flows among the references
		resolveDataFlows(referenceManager);
		
		// Step 5: Print results
		printResults(referenceManager);
		
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Done in " + timer.getElapsedSecondsInText() + ".");
		return referenceManager;
	}
	
	/**
	 * Creates the data model & find PHP/SQL references in the PHP code
	 */
	private DataModel createDataModelAndFindPhpSqlReferences(ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Creating data model and finding PHP/SQL references...");
		
		ReferenceDetector.findReferencesInPhpCode(phpFile, referenceManager);
		
		DataModel dataModel = new PhpExecuter().execute(phpFile);
		
		ReferenceDetector.findReferencesInPhpCodeFinished();
		
		return dataModel;
	}
	
	/**
	 * Parses the DataModel
	 */
	private HtmlDocument parseDataModel(DataModel dataModel) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Parsing data model...");
		
		return new ParseDataModel().parse(dataModel);
	}
	
	/**
	 * Finds HTML/JavaScript references in the HTML document
	 */
	private void findReferencesInHtmlDocument(HtmlDocument htmlDocument, ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Finding HTML/JavaScript references...");

		ReferenceDetector.findReferencesInHtmlDocument(htmlDocument, phpFile, referenceManager);
	}
	
	/**
	 * Resolves data flows among the references
	 */
	private void resolveDataFlows(ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Resolving data flows...");
		
		referenceManager.getDataFlowManager().resolveDataFlows();
	}
	
	/**
	 * Prints results
	 */
	private void printResults(ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Printing results...");
		
		System.out.println(referenceManager.writeReferenceListToText());
		//new XmlReadWrite().printReferencesToXmlFile(referenceManager.getSortedReferenceList(), new File("/Users/HUNG/Desktop/Dataflows.xml");
	}
	
}

package edu.iastate.analysis.references.detection;

import java.io.File;

import edu.iastate.analysis.references.ReferenceManager;
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
	
	public static String PHP_FILE = "/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/index.php";
	public static String XML_FILE = "/Users/HUNG/Desktop/Dataflows.xml";
	
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
		
		// Step 1: Create the data model
		ReferenceManager referenceManager = new ReferenceManager();
		DataModel dataModel = createDataModel(referenceManager);
		
		// Step 2: Parse the data model
		HtmlDocument htmlDocument = new ParseDataModel().parse(dataModel);
		
		// Step 2: Find references in the HTML document
		//TODO Comment out the statement below to stop detecting embedded entities.
		findReferencesInHtmlDocument(htmlDocument, referenceManager);
		
		// Step 3: Print results
		printResults(referenceManager);
		
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		return referenceManager;
	}
	
	/**
	 * Creates the data model.
	 */
	private DataModel createDataModel(final ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Creating data model...");
		
		//ReferenceDetector.findReferencesInPhpCode(phpFile, referenceManager); // Also find references while building D-model (e.g. $_REQUEST variables)
		
		DataModel dataModel = new PhpExecuter().execute(phpFile);
		
		//ReferenceDetector.findReferencesInPhpCodeFinished();
		
		return dataModel;
	}
	
	/**
	 * Finds references in the HtmlDocument.
	 */
	private void findReferencesInHtmlDocument(HtmlDocument htmlDocument, ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Finding references from HtmlDocument...");

		ReferenceDetector.findReferencesInHtmlDocument(htmlDocument, referenceManager);
	}
	
	/**
	 * Prints the results
	 */
	private void printResults(ReferenceManager referenceManager) {
		new XmlReadWrite().printReferencesToXmlFile(referenceManager.getReferenceList(), new File(XML_FILE));
	}
	
}

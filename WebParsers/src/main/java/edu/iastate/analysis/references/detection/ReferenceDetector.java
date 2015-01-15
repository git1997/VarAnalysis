package edu.iastate.analysis.references.detection;

import java.io.File;

import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.htmlparser.DataModelParser;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;

/**
 * 
 * @author HUNG
 *
 */
public class ReferenceDetector {
	
	/**
	 * Executes PHP code and returns detected references
	 * @param file The file to be executed
	 */
	public ReferenceManager detect(File file) {
		ReferenceManager referenceManager = new ReferenceManager();
		
		// Step 1: Create the data model & find PHP/SQL references in the PHP code
		ReferenceFinder.findReferencesInPhpCode(file, referenceManager);
		DataModel dataModel = new PhpExecuter().execute(file);
		ReferenceFinder.findReferencesInPhpCodeFinished();
		
		// Step 2: Parse the DataModel
		HtmlDocument htmlDocument = new DataModelParser().parse(dataModel);
		
		// Step 3: Find HTML/JavaScript references in the HTML document
		ReferenceFinder.findReferencesInHtmlDocument(htmlDocument, file, referenceManager);
		
		// Step 4: Resolve data flows among the references
		referenceManager.getDataFlowManager().resolveDataFlows();
				
		return referenceManager;
	}

}

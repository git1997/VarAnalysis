package edu.iastate.parsers.html.core;

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
public class PhpExecuterAndParser {
	
	/**
	 * Executes PHP code and returns an HtmlDocument
	 * @param file The file to be executed
	 */
	public HtmlDocument executeAndParse(File file) {
		// Step 1: Execute the file and get its DataModel
		DataModel dataModel = new PhpExecuter().execute(file);
		
		// Step 2: Parse the dataModel into an HtmlDocument
		HtmlDocument htmlDocument = new DataModelParser().parse(dataModel);
				
		return htmlDocument;
	}
	
}
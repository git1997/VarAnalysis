package edu.iastate.webslice.core;

import java.io.File;
import java.util.ArrayList;

import edu.iastate.parsers.html.core.PhpExecuterAndParser;
import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.symex.util.FileIO;

/**
 * This class is used to collect entry points in a PHP web application.
 * 
 * @author HUNG
 *
 */
public class GetEntryPoints {
	
	public static void main(String[] args) {
		String projectPath = SubjectSystems.projectPath;
		ArrayList<String> entries = new ArrayList<String>();
		
		for (String file : FileIO.getAllFilesInFolderByExtensions(projectPath, new String[]{".php"})) {
			PhpExecuterAndParser phpExecuterAndParser = new PhpExecuterAndParser();
			HtmlDocument htmlDocument = phpExecuterAndParser.executeAndParse(new File(file));
			
			for (HtmlNode htmlNode : htmlDocument.getTopNodes())
				if (checkContainingHtmlTag(htmlNode)) {
					entries.add(file);
					break;
				}
		}
		
		for (String entry : entries)
			System.out.println("\"" + entry.substring(projectPath.length() + 1) + "\",");
	}
	
	private static boolean checkContainingHtmlTag(HtmlNode htmlNode) {
		if (htmlNode instanceof HtmlSelect) {
			return checkContainingHtmlTag(((HtmlSelect) htmlNode).getTrueBranchNode()) || checkContainingHtmlTag(((HtmlSelect) htmlNode).getFalseBranchNode());
		}
		else if (htmlNode instanceof HtmlConcat) {
			for (HtmlNode childNode : ((HtmlConcat) htmlNode).getChildNodes())
				if (checkContainingHtmlTag(childNode))
					return true;
			return false;
		}
		else if (htmlNode instanceof HtmlElement) {
			return ((HtmlElement) htmlNode).getType().equals("html");
		}
		else
			return false;
	}

}

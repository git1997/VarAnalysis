package edu.iastate.webtesting.bugcoverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;

import edu.iastate.webtesting.evaluation.DebugInfo;
import edu.iastate.webtesting.values_clone.CondValue;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeSpellingBugCoverage {
	
	public Set<String> compute(String output, CondValue cModel) {
		List<String> clientBugs = getSpellingBugs(output);
		List<String> serverBugs = ComputeValidationBugCoverage.clientToServerBugs(clientBugs, cModel);
		Set<String> bugs = ComputeValidationBugCoverage.filterServerBugs(serverBugs);
		
		// For debugging
		DebugInfo.spellingBugCoverageComputed(clientBugs, serverBugs, bugs);
		
		return bugs;
	}
	
	private static List<String> getSpellingBugs(String output) {
		ArrayList<String> spellingErrors = new ArrayList<String>();
	
		try {			
			Parser parser = new Parser(output);	// To run correctly, the method org.htmlparser.Parser.setResource(String) must be slightly modified
			for (NodeIterator iterator = parser.elements(); iterator.hasMoreNodes(); ) {
				org.htmlparser.Node nextHtmlNode = iterator.nextNode();
				visitNode(nextHtmlNode, spellingErrors);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return spellingErrors;
	}
	
	private static void visitNode(org.htmlparser.Node node, ArrayList<String> spellingErrors) {
		if (node instanceof TextNode) {
			TextNode textNode = (TextNode) node;
			String text = textNode.getText();
			
			// Handle template text in the form of <# .... #>
			if (text.trim().startsWith("<#") && text.trim().endsWith("#>"))
				return;
			
			int begin = 0;
			while (begin < text.length()) {
				// Eat whitespace
				while (begin < text.length() && isWordSeparator(text.charAt(begin)))
					begin++;
				if (begin == text.length())
					break;
				
				// Eat non-whitespace
				int end = begin + 1; // exclusive
				while (end < text.length() && !isWordSeparator(text.charAt(end)))
					end++;
				
				int position = textNode.getStartPosition() + begin;
				String word = text.substring(begin, end);
				begin = end;
				
				if (SpellChecker.hasSpellingError(word))
					spellingErrors.add("offset " + position + " line 0 column 0: " + word);
			}
		}
		else if (node instanceof TagNode) {
			TagNode tag = (TagNode) node;
			if (tag.getTagName().equalsIgnoreCase("script") || tag.getTagName().equalsIgnoreCase("style"))
				return;
		}
			
		org.htmlparser.util.NodeList nodeList = node.getChildren();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.size(); i++) {
				org.htmlparser.Node childNode = nodeList.elementAt(i);
				visitNode(childNode, spellingErrors);
			}
		}
	}
	
	private static boolean isWordSeparator(char c) {
		return Character.isWhitespace(c) 
					|| c == '`' || c == '~' || c == '!' || c == '@' || c == '#' || c == '$' || c == '%' || c == '^' || c == '&' || c == '*' || c == '(' || c == ')' || c == '-' || c == '_' || c == '=' || c == '+'
					|| c == '[' || c == '{' || c == ']' || c == '}' || c == '\\' || c == '|'
					|| c == ':' || c == ';' || c == '"' || c == ',' || c == '<' || c == '.' || c == '>' || c == '/' || c == '?';
	}
}

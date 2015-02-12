package edu.iastate.varis.ui.highlighters;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.jface.text.Position;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.sax.nodes.HCloseTag;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;

/**
 * 
 * @author HUNG
 *
 */
public class SemanticHighlightingManager {
	
	private static SemanticHighlightingManager instance = new SemanticHighlightingManager();

	private HashMap<File, LinkedList<Position>> htmlTagNamePositions = new HashMap<File, LinkedList<Position>>();
	private HashMap<File, LinkedList<Position>> htmlAttrNamePositions = new HashMap<File, LinkedList<Position>>();
	private HashMap<File, LinkedList<Position>> htmlAttrValuePositions = new HashMap<File, LinkedList<Position>>();
	
	public static SemanticHighlightingManager getInstance() {
		return instance;
	}
	
	public void addHtmlDocument(HtmlDocument htmlDocument) {
		new HtmlVisitor().visitDocument(htmlDocument);
	}
	
	public void removeHtmlDocuments() {
		htmlTagNamePositions = new HashMap<File, LinkedList<Position>>();
		htmlAttrNamePositions = new HashMap<File, LinkedList<Position>>();
		htmlAttrValuePositions = new HashMap<File, LinkedList<Position>>();
	}
	
	public LinkedList<Position> getHtmlTagNamePositions(File file) {
		if (htmlTagNamePositions.containsKey(file))
			return new LinkedList<Position>(htmlTagNamePositions.get(file));
		else
			return new LinkedList<Position>();
	}
	
	public LinkedList<Position> getHtmlAttrNamePositions(File file) {
		if (htmlAttrNamePositions.containsKey(file))
			return new LinkedList<Position>(htmlAttrNamePositions.get(file));
		else
			return new LinkedList<Position>();
	}
	
	public LinkedList<Position> getHtmlAttrValuePositions(File file) {
		if (htmlAttrValuePositions.containsKey(file))
			return new LinkedList<Position>(htmlAttrValuePositions.get(file));
		else
			return new LinkedList<Position>();
	}
	
	class HtmlVisitor extends HtmlDocumentVisitor {
		
		public void visitElement(HtmlElement htmlElement) {
			super.visitElement(htmlElement);
			
			addPosition(htmlTagNamePositions, htmlElement.getOpenTag().getLocation());
			
			for (HtmlToken endBracket : htmlElement.getOpenTag().getEndBrackets()) {
				addPosition(htmlTagNamePositions, endBracket.getLocation());
			}
			
			for (HCloseTag closeTag : htmlElement.getCloseTags()) {
				addPosition(htmlTagNamePositions, closeTag.getLocation());
			}
		}
		
		public void visitAttribute(HtmlAttribute htmlAttribute) {
			super.visitAttribute(htmlAttribute);
			
			addPosition(htmlAttrNamePositions, htmlAttribute.getLocation());
		}
		
		public void visitAttributeValue(HtmlAttributeValue htmlAttributeValue) {
			super.visitAttributeValue(htmlAttributeValue);
			
			addPosition(htmlAttrValuePositions, htmlAttributeValue.getLocation());
		}
		
		private void addPosition(HashMap<File, LinkedList<Position>> positions, PositionRange location) {
			for (Range range : location.getRanges()) {
				if (!range.isUndefined()) {
					if (!positions.containsKey(range.getFile()))
						positions.put(range.getFile(), new LinkedList<Position>());
					positions.get(range.getFile()).add(new Position(range.getOffset(), range.getLength()));
				}
			}
		}
		
	}

}

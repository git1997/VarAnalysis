package edu.iastate.webtesting.visualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlDocumentVisitor;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
import edu.iastate.parsers.html.htmlparser.DataModelParser;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.ReadWriteDataModelToFromXml;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.util.FileIO;
import edu.iastate.symex.util.StringUtils;

/**
 * 
 * @author HUNG
 *
 */
public class WriteHtmlDocumentToHtml extends HtmlDocumentVisitor {
	
	public static final String WORKSPACE = "/Users/HUNG/Desktop/Web Testing Demo";
	
	public static final String DATA_MODEL_FILE = WORKSPACE + "/input/data-model.xml";
	public static final String OUTPUT_FILE = WORKSPACE + "/output.html";
	
	public static final String MAIN_TEMPLATE_FILE = WORKSPACE + "/template/template-main.html";
	public static final String SWITCH_TEMPLATE_FILE = WORKSPACE + "/template/template-switch.html";
	public static final String CASE_TEMPLATE_FILE = WORKSPACE + "/template/template-case.html";
	
	private static final String mainTemplate = FileIO.readStringFromFile(MAIN_TEMPLATE_FILE);
	private static final String switchTemplate = FileIO.readStringFromFile(SWITCH_TEMPLATE_FILE);
	private static final String caseTemplate = FileIO.readStringFromFile(CASE_TEMPLATE_FILE);
	
	private StringBuilder strBuilder;
	private int depth; // Depth of the current node during traversal for pretty printing
	
	// COVERAGE SUPPORT
	private boolean coverageSupport = false; // Set to true to print coverage information
	private Map<Constraint, Float> trueBranchCoverage = null;
	private Map<Constraint, Float> falseBranchCoverage = null;
	
	/**
	 * Main entry
	 */
	public static void main(String[] args) {
		DataModel dataModel = new ReadWriteDataModelToFromXml().readDataModelFromXmlFile(DATA_MODEL_FILE);
		HtmlDocument htmlDocument = new DataModelParser().parse(dataModel);
		String html = new WriteHtmlDocumentToHtml().convertToHtml(htmlDocument);
		FileIO.writeStringToFile(html, OUTPUT_FILE);
	}
	
	public WriteHtmlDocumentToHtml() {
		this.strBuilder = new StringBuilder();
		this.depth = 0;
	}
	
	/**
	 * Converts an HtmlDocument with #ifdefs to HTML format without #ifdefs
	 */
	public String convertToHtml(HtmlDocument htmlDocument) {
		visitDocument(htmlDocument);
		String mainContent = getResults();
		return mainTemplate.replace("MAIN_CONTENT", mainContent);
	}
	
	public String getResults() {
		return strBuilder.toString();
	}
	
	// COVERAGE SUPPORT
	public void setBranchCoverages(Map<Constraint, Float> trueBranchCoverage, Map<Constraint, Float> falseBranchCoverage) {
		this.coverageSupport = true;
		this.trueBranchCoverage = trueBranchCoverage;
		this.falseBranchCoverage = falseBranchCoverage;
	}
	
	@Override
	public void visitSelect(HtmlSelect htmlSelect) {
		Switch switch_ = convertSelectToSwitch(htmlSelect);
		String switchType = "switch-text";
		
		StringBuilder savedString = strBuilder;
		StringBuilder casesHtml = new StringBuilder();
		
		for (Case case_ : switch_.getCases()) {
			strBuilder = new StringBuilder();
			visitNode(case_.getData());
			String caseContent = strBuilder.toString();
			
			if (!(case_.getData() instanceof HtmlText))
				switchType = "switch-elements";
			
			// COVERAGE SUPPORT
			String coverageString;
			if (case_.getCoverage() != null)
				coverageString = "Coverage: " + (int) (case_.getCoverage() * 100) + "% ";
			else
				coverageString = "";
			
			String caseHtml = caseTemplate
									.replace("CASE_CONSTRAINT_STRING", coverageString + case_.getConstraint().toDebugString())
									.replace("CASE_CONSTRAINT_LOCATION", case_.getConstraint().getLocation().getStartPosition().toDebugString())
									.replace("CASE_CONTENT", caseContent);
			casesHtml.append(caseHtml);
		}
		String switchHtml = switchTemplate
								.replace("SWITCH_TYPE", switchType)
								.replace("CASES", casesHtml.toString()); 
		
		strBuilder = savedString;
		strBuilder.append(switchHtml);
	}
	
	private HashSet<String> allowedTags = new HashSet<String>(
			Arrays.asList(new String[] {
					"h1", "h2", "p",
					"b", "a", "font", "span", "div", "center",
					"br", "empty",
					"form", "input", "select", "option",
					"table", "th", "tr", "td",
					"img"
				})
			);
	
	private HashSet<String> allowedAttrsSet = new HashSet<String>(
			Arrays.asList(new String[] {
					"color", "bgcolor",
					"align", "valign", "colspan",
					"type", "name", "value", "target", "href", "maxlength", "src",
					"action", "method",
					"background"
				})
			);
	
	private HashSet<String> disallowedAttrsSet = new HashSet<String>(
			Arrays.asList(new String[] {
					"class",
					"width", "height",
					"border", "cellspacing", "cellpadding",
					"onmouseover", "onmouseout", "onclick", "onload",
					"style",
					"value" // Consider this?
				})
			);
	
	private ArrayList<HtmlAttribute> getAllowedAttrs(ArrayList<HtmlAttribute> attrs) {
		ArrayList<HtmlAttribute> allowedAttrs = new ArrayList<HtmlAttribute>();
		for (HtmlAttribute attr : attrs) {
			if (attr.getConstraint().isTautology() && allowedAttrsSet.contains(attr.getName()))
				allowedAttrs.add(attr);
		}
		return allowedAttrs;
	}
	
	private ArrayList<HtmlAttribute> getDisallowedAttrs(ArrayList<HtmlAttribute> attrs) {
		ArrayList<HtmlAttribute> disallowedAttrs = new ArrayList<HtmlAttribute>();
		for (HtmlAttribute attr : attrs) {
			if (disallowedAttrsSet.contains(attr.getName()))
				disallowedAttrs.add(attr);
		}
		return disallowedAttrs;
	}
	
	@Override
	public void visitElement(HtmlElement htmlElement) {
		if (htmlElement.getType().equals("style") || htmlElement.getType().equals("script"))
			return;
		
		ArrayList<HtmlAttribute> attrs = htmlElement.getAttributes();
		ArrayList<HtmlAttribute> allowedAttrs = getAllowedAttrs(attrs);
		ArrayList<HtmlAttribute> disallowedAttrs = getDisallowedAttrs(attrs);
		ArrayList<HtmlAttribute> remainingAttrs = new ArrayList<HtmlAttribute>(attrs);
		remainingAttrs.removeAll(allowedAttrs);
		remainingAttrs.removeAll(disallowedAttrs);
		
		strBuilder.append(StringUtils.getIndentedTabs(depth));
		if (allowedTags.contains(htmlElement.getType()))
			strBuilder.append("<" + htmlElement.getType() + printAllowedAttrs(allowedAttrs) + ">" + System.lineSeparator());
		else
			strBuilder.append("&lt;" + htmlElement.getType() + printAllowedAttrs(allowedAttrs) + "&gt;" + System.lineSeparator());
		
		for (HtmlAttribute attr : remainingAttrs) {
			if (attr.getStringValue().matches("11+"))
				strBuilder.append(attr.getName() + " = \"" + writeSymbolicValue(attr.getAttributeValue().getLocation())+ "\" ");
			else
				strBuilder.append(attr.getName() + " = \"" + attr.getStringValue() + "\" ");
		}
		
		depth++;
		
		super.visitElement(htmlElement);
	    
	    depth--;
	    strBuilder.append(StringUtils.getIndentedTabs(depth));
	    if (allowedTags.contains(htmlElement.getType()))
	    	strBuilder.append("</" + htmlElement.getType() + ">" + System.lineSeparator());
	    else
	    	strBuilder.append("&lt;/" + htmlElement.getType() + "&gt;" + System.lineSeparator());
	}
	
	private String printAllowedAttrs(ArrayList<HtmlAttribute> attrs) {
		StringBuilder str = new StringBuilder();
		for (HtmlAttribute attr : attrs) {
			str.append(" " + attr.getName() + " = \"" + attr.getStringValue() + "\"");
		}
		return str.toString();
	}
	
	private String writeSymbolicValue(PositionRange location) {
		Position position = location.getStartPosition();
		int length = location.getLength();
		String phpCode = FileIO.readStringFromFile(position.getFile()).substring(position.getOffset(), position.getOffset() + length);
		return "<code title=\"" + position.toDebugString() + "\">" + phpCode + "</code>";
	}
	
	@Override 
	public void visitText(HtmlText htmlText) {
		if (htmlText.getStringValue().matches("1+"))
			strBuilder.append(writeSymbolicValue(htmlText.getLocation()) + System.lineSeparator());
		else
			strBuilder.append(htmlText.getStringValue() + System.lineSeparator());
	}
	
	/*
	 * Utility classes
	 */
	
	private class Switch {
		
		private ArrayList<Case> cases = new ArrayList<Case>();
		
		public Switch(ArrayList<Case> cases) {
			this.cases = cases;
		}
		
		public ArrayList<Case> getCases() {
			return new ArrayList<Case>(cases);
		}
		
	}
	
	private class Case {
		
		private Constraint constraint;
		private HtmlNode data;
		
		// COVERAGE SUPPORT
		private Float coverage = null;
		
		public Case(Constraint constraint, HtmlNode data) {
			this.constraint = constraint;
			this.data = data;
		}
		
		public Constraint getConstraint() {
			return constraint;
		}
		
		public HtmlNode getData() {
			return data;
		}
		
		// COVERAGE SUPPORT
		public void setCoverage(Float coverage) {
			this.coverage = coverage;
		}
		
		// COVERAGE SUPPORT
		public Float getCoverage() {
			return coverage;
		}
		
	}
	
	/*
	 * Utility methods
	 */
	
	private Switch convertSelectToSwitch(HtmlSelect select) {
		ArrayList<Case> cases = new ArrayList<Case>();
		Constraint constraint = select.getConstraint();
		
		if (select.getTrueBranchNode() instanceof HtmlSelect)
			cases.addAll(convertSelectToSwitch((HtmlSelect) select.getTrueBranchNode()).getCases()); // Consider adding select.getConstraint() ?
		else {
			Case case_ = new Case(constraint, select.getTrueBranchNode());
			
			// COVERAGE SUPPORT
			if (coverageSupport) {
				if (trueBranchCoverage.containsKey(constraint))
					case_.setCoverage(trueBranchCoverage.get(constraint));
				else
					case_.setCoverage((float) -1); // -1 means unresolved
			}
			
			cases.add(case_);
		}
		
		if (select.getFalseBranchNode() instanceof HtmlSelect)
			cases.addAll(convertSelectToSwitch((HtmlSelect) select.getFalseBranchNode()).getCases()); // Consider adding select.getConstraint() ?
		else {
			Case case_ = new Case(ConstraintFactory.createNotConstraint(constraint), select.getFalseBranchNode());
			
			// COVERAGE SUPPORT
			if (coverageSupport) {
				if (falseBranchCoverage.containsKey(constraint))
					case_.setCoverage(falseBranchCoverage.get(constraint));
				else
					case_.setCoverage((float) -1); // -1 means unresolved
			}

			cases.add(case_);
		}
		
		return new Switch(cases);
	}
	
}
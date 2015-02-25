package edu.iastate.webslice.core;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.webslice.core.AstAnalyzer.PositionInfo;

/**
 * 
 * @author HUNG
 *
 */
public class ShowStatisticsOnReferences {
	
	private String[] referenceTypes = {
		"PhpVariableDecl",
		"PhpVariableRef",
		"PhpRefToHtml",
		"PhpFunctionDecl",
		"PhpFunctionCall",
		"HtmlFormDecl",
		"HtmlInputDecl",
		"HtmlDeclOfHtmlInputValue",
		"HtmlQueryDecl",
		"HtmlIdDecl",
		"JsRefToHtmlForm",
		"JsRefToHtmlInput",
		"JsDeclOfHtmlInputValue",
		"JsRefToHtmlInputValue",
		"JsRefToHtmlId",
		"JsVariableDecl",
		"JsVariableRef",
		"JsFunctionDecl",
		"JsFunctionCall",
		"SqlTableColumnDecl",
		"PhpRefToSqlTableColumn"
	};
	
	private String[] languages = {
		"PHP", "SQL", "HTML", "JS"
	};
	
	/**
	 * Shows statistics on the references in referenceManager.
	 */
	public String showStatistics(ReferenceManager referenceManager) {
		StringBuilder str = new StringBuilder();

		str.append("========== STATISTICS OF DATA-FLOW GRAPH ==========" + System.lineSeparator());
		showStatisticsOfDataFlowGraph(referenceManager, str);
		
		str.append("==========STATISTICS OF SLICES ==========" + System.lineSeparator());
		showStatisticsOfSlices(referenceManager, str);
		
		return str.toString();
	}
	
	/**
	 * Shows statistics of the data-flow graph
	 */
	private void showStatisticsOfDataFlowGraph(ReferenceManager referenceManager, StringBuilder str) {
		int totalNodes = 0;
		
		LinkedHashMap<String, Integer> nodesByType = new LinkedHashMap<String, Integer>();
		for (String type : referenceTypes)
			nodesByType.put(type, 0);
		
		LinkedHashMap<String, Integer> nodesByLanguage = new LinkedHashMap<String, Integer>();
		for (String language : languages)
			nodesByLanguage.put(language, 0);
		
		int embeddedNodes = 0;
		int embeddedAndOnEchoPrintNodes = 0;
		
		for (Reference reference : referenceManager.getReferenceList()) {
			Node node = new Node(reference);
			
			String type = node.getType();
			String language = node.getLanguage();
			boolean isEmbedded = node.isEmbedded();
			boolean isEmbeddedAndOnEchoPrint = node.isEmbeddedAndOnEchoPrint();
			
			totalNodes++;
			nodesByType.put(type, nodesByType.get(type) + 1);
			nodesByLanguage.put(language, nodesByLanguage.get(language) + 1);
			if (isEmbedded)
				embeddedNodes++;
			if (isEmbeddedAndOnEchoPrint)
				embeddedAndOnEchoPrintNodes++;
		}
		
		int totalEdges = 0;
		int crossLangEdges = 0;
		int crossFileEdges = 0;
		int crossFuncEdges = 0;
		int crossStringEdges = 0;
		int crossEntryEdges = 0;
		
		for (Reference ref1 : referenceManager.getReferenceList())
		for (Reference ref2 : referenceManager.getDataFlowManager().getDataFlowFrom(ref1)) {
			Edge edge = new Edge(ref1, ref2);
			
			totalEdges++;
			if (edge.isCrossLanguage())
				crossLangEdges++;
			if (edge.isCrossFile())
				crossFileEdges++;
			if (edge.isCrossFunction())
				crossFuncEdges++;
			if (edge.isCrossString())
				crossStringEdges++;
			if (edge.isCrossEntry())
				crossEntryEdges++;
		}
		
		str.append("Total nodes: " + totalNodes + System.lineSeparator());
		for (String type : nodesByType.keySet()) {
			str.append(type  + ": " + nodesByType.get(type) + System.lineSeparator());
		}
		for (String language : nodesByLanguage.keySet()) {
			str.append(language  + ": " + nodesByLanguage.get(language) + System.lineSeparator());
		}
		str.append("Embedded: " + embeddedNodes + System.lineSeparator());
		str.append("EmbeddedAndOnEchoPrint: " + embeddedAndOnEchoPrintNodes + System.lineSeparator());
		
		str.append("Total edges: " + totalEdges + System.lineSeparator());
		str.append("Cross-language edges: " + crossLangEdges + System.lineSeparator());
		str.append("Cross-file edges: " + crossFileEdges + System.lineSeparator());
		str.append("Cross-function edges: " + crossFuncEdges + System.lineSeparator());
		str.append("Cross-string edges: " + crossStringEdges + System.lineSeparator());
		str.append("Cross-entry edges: " + crossEntryEdges + System.lineSeparator());
		
		str.append(totalNodes + "\t" + nodesByLanguage.get("PHP") + "\t" + nodesByLanguage.get("SQL") + "\t" + nodesByLanguage.get("HTML") + "\t" + nodesByLanguage.get("JS") + "\t" 
						+ embeddedNodes + "\t" + (embeddedNodes - embeddedAndOnEchoPrintNodes) + "\t"
						+ totalEdges + "\t" + crossLangEdges + "\t" + crossFileEdges + "\t" + crossFuncEdges + "\t" + crossStringEdges + "\t" + crossEntryEdges + System.lineSeparator());
	}
	
	/**
	 * Shows statistics of the slices
	 */
	private void showStatisticsOfSlices(ReferenceManager referenceManager, StringBuilder str) {
		int totalSlices = 0;
		TreeMap<Integer, Integer> edgesCount = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> lengthCount = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> crossLangEdgesCount = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> crossFileEdgesCount = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> crossFuncEdgesCount = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> crossStringEdgesCount = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> crossEntryEdgesCount = new TreeMap<Integer, Integer>();

		for (Reference reference : referenceManager.getReferenceList()) {
			int[] length_ = new int[1];
			HashSet<Node> nodes = new HashSet<Node>();
			HashSet<Edge> edges = new HashSet<Edge>();
			getForwardSlice(referenceManager, reference, nodes, edges, length_);
			
			// Discard slices with 1 node
			if (edges.isEmpty())
				continue;
			
			totalSlices++;
			int numOfEdges = edges.size();
			int length = length_[0];
			int crossLangEdges = 0;
			int crossFileEdges = 0;
			int crossFuncEdges = 0;
			int crossStringEdges = 0;
			int crossEntryEdges = 0;
			
			for (Edge edge : edges) {
				if (edge.isCrossLanguage())
					crossLangEdges++;
				if (edge.isCrossFile())
					crossFileEdges++;
				if (edge.isCrossFunction())
					crossFuncEdges++;
				if (edge.isCrossString())
					crossStringEdges++;
				if (edge.isCrossEntry())
					crossEntryEdges++;
			}
			
			incrementCount(edgesCount, numOfEdges);
			incrementCount(lengthCount, length);
			incrementCount(crossLangEdgesCount, crossLangEdges);
			incrementCount(crossFileEdgesCount, crossFileEdges);
			incrementCount(crossFuncEdgesCount, crossFuncEdges);
			incrementCount(crossStringEdgesCount, crossStringEdges);
			incrementCount(crossEntryEdgesCount, crossEntryEdges);
		}
		
		str.append("Total slices: " + totalSlices + System.lineSeparator());
		
		str.append("Edges:" + System.lineSeparator());
		reportDistribution(edgesCount, str);
		str.append("----------" + System.lineSeparator());
		
		str.append("Length:" + System.lineSeparator());
		reportDistribution(lengthCount, str);
		str.append("----------" + System.lineSeparator());
		
		str.append("Cross-language edges:" + System.lineSeparator());
		reportDistribution(crossLangEdgesCount, str);
		str.append("----------" + System.lineSeparator());
		
		str.append("Cross-file edges:" + System.lineSeparator());
		reportDistribution(crossFileEdgesCount, str);
		str.append("----------" + System.lineSeparator());
		
		str.append("Cross-function edges:" + System.lineSeparator());
		reportDistribution(crossFuncEdgesCount, str);
		str.append("----------" + System.lineSeparator());
		
		str.append("Cross-string edges:" + System.lineSeparator());
		reportDistribution(crossStringEdgesCount, str);
		str.append("----------" + System.lineSeparator());
		
		str.append("Cross-entry edges:" + System.lineSeparator());
		reportDistribution(crossEntryEdgesCount, str);
		str.append("----------" + System.lineSeparator());
		
		if (!crossLangEdgesCount.containsKey(0))
			crossLangEdgesCount.put(0, 0);
		if (!crossFileEdgesCount.containsKey(0))
			crossFileEdgesCount.put(0, 0);
		if (!crossFuncEdgesCount.containsKey(0))
			crossFuncEdgesCount.put(0, 0);
		if (!crossStringEdgesCount.containsKey(0))
			crossStringEdgesCount.put(0, 0);
		if (!crossEntryEdgesCount.containsKey(0))
			crossEntryEdgesCount.put(0, 0);
		
		str.append(totalSlices + " & " 
				+ getPercentile(edgesCount, 0.5) + " & "
				+ getPercentile(lengthCount, 0.5) + " & "
				+ (totalSlices - crossLangEdgesCount.get(0)) + " & "
				+ (totalSlices - crossFileEdgesCount.get(0)) + " & "
				+ (totalSlices - crossFuncEdgesCount.get(0)) + " & "
				+ (totalSlices - crossStringEdgesCount.get(0)) + " & "
				+ (totalSlices - crossEntryEdgesCount.get(0)));		
	}
	
	/**
	 * Computes the forward slice of a reference
	 */
	private void getForwardSlice(ReferenceManager referenceManager, Reference reference, HashSet<Node> nodes, HashSet<Edge> edges, int[] length) {
		HashSet<Reference> referencesInSlice = new HashSet<Reference>();
		referencesInSlice.add(reference);
		nodes.add(new Node(reference));
		length[0] = 1;
		
		HashSet<Reference> toExpandReferences = new HashSet<Reference>();
		toExpandReferences.add(reference);
		
		while (!toExpandReferences.isEmpty()) {
			HashSet<Reference> toExpandReferences_ = new HashSet<Reference>(toExpandReferences);
			toExpandReferences.clear();
			
			for (Reference ref1 : toExpandReferences_) {
				for (Reference ref2 : referenceManager.getDataFlowManager().getDataFlowFrom(ref1)) {
					//if (ref2.getType().equals("PhpRefToHtml"))
					//	continue;
					if (!referencesInSlice.contains(ref2)) {
						referencesInSlice.add(ref2);
						nodes.add(new Node(ref2));
						edges.add(new Edge(ref1, ref2));
						
						toExpandReferences.add(ref2);
					}
				}
			}
			
			if (!toExpandReferences.isEmpty())
				length[0]++;
		}
	}
	
	/**
	 * Increments the count for a given type
	 */
	private void incrementCount(TreeMap<Integer, Integer> countMap, int type) {
		if (!countMap.containsKey(type))
			countMap.put(type, 0);
		countMap.put(type, countMap.get(type) + 1);
	}
	
	/**
	 * Shows distribution of a countMap
	 */
	private void reportDistribution(TreeMap<Integer, Integer> countMap, StringBuilder str) {
		int totalCount = 0;
		int zeroCount = countMap.containsKey(0) ? countMap.get(0) : 0;
		int sum = 0;
		int max = 0;
		for (Integer type : countMap.keySet()) {
			totalCount += countMap.get(type);
			sum += countMap.get(type) * type;
			if (type > max)
				max = type;
		}
		double average = (double) sum / totalCount;
		
		str.append("Total: " + totalCount + "\t");
		str.append("ZeroCount: " + zeroCount + "\t");
		str.append("Average: " + String.format("%.1f", average) + "\t");
		str.append("Max: " + max + "\t");
		str.append("Percentile 25%: " + getPercentile(countMap, 0.25) + "\t");
		str.append("Percentile 50%: " + getPercentile(countMap, 0.50) + "\t");
		str.append("Percentile 75%: " + getPercentile(countMap, 0.75) + "\t");
		str.append("Percentile 100%: " + getPercentile(countMap, 1) + "\t");
		str.append(System.lineSeparator());
	}
	
	/**
	 * Returns the type of a countMap at a percentile.
	 */
	private int getPercentile(TreeMap<Integer, Integer> countMap, double percentile) {
		int totalCount = 0;
		for (Integer type : countMap.keySet()) {
			totalCount += countMap.get(type);
		}
		
		int index = (int) (totalCount * percentile);
		
		int idx = 0;
		for (Integer type : countMap.keySet()) {
			idx += countMap.get(type);
			if (idx >= index)
				return type;
		}
		return -1;
	}
	
	/**
	 * This class represents a node in a data-flow graph
	 */
	private class Node {
		
		private Reference reference;
		
		private PositionInfo positionInfo;
		
		public Node(Reference reference) {
			this.reference = reference;
			this.positionInfo = AstAnalyzer.inst.getPositionInfo(reference.getStartPosition());
		}
		
		public Reference getReference() {
			return reference;
		}
		
		public PositionInfo getPositionInfo() {
			return positionInfo;
		}
		
		public String getType() {
			return reference.getType();
		}
		
		public String getLanguage() {
			if (reference.getType().startsWith("Php"))
				return "PHP";
			else if (reference.getType().startsWith("Sql"))
				return "SQL";
			else if (reference.getType().startsWith("Html"))
				return "HTML";
			else if (reference.getType().startsWith("Js"))
				return "JS";
			else
				return "Unknown";
		}
		
		public boolean isEmbedded() {
			return !getLanguage().equals("PHP") && positionInfo.getScalar() != null;
		}
		
		public boolean isEmbeddedAndOnEchoPrint() {
			return isEmbedded() && positionInfo.getEchoPrintStatement() != null;
		}
		
	}
	
	/**
	 * This class represents an edge in a data-flow graph
	 */
	private class Edge {
		
		private Node ref1;
		
		private Node ref2;
		
		public Edge(Reference ref1, Reference ref2) {
			this.ref1 = new Node(ref1);
			this.ref2 = new Node(ref2);
		}
		
		public boolean isCrossLanguage() {
			return !ref1.getLanguage().equals(ref2.getLanguage());
		}
		
		public boolean isCrossFile() {
			return (!ref1.getReference().getLocation().getStartPosition().getFile().equals(ref2.getReference().getLocation().getStartPosition().getFile()));
		}
		
		public boolean isCrossFunction() {
			return (ref1.getPositionInfo().getFunctionDeclaration() != ref2.getPositionInfo().getFunctionDeclaration());
		}
		
		public boolean isCrossString() {
			ASTNode astNode1 = ref1.getPositionInfo().getInLineHtml();
			if (astNode1 == null)
				astNode1 = ref1.getPositionInfo().getScalar();
			
			ASTNode astNode2 = ref2.getPositionInfo().getInLineHtml();
			if (astNode2 == null)
				astNode2 = ref2.getPositionInfo().getScalar();
			
			return (astNode1 != astNode2);
		}
		
		public boolean isCrossEntry() {
			return ref2.getType().equals("PhpRefToHtml");
		}
		
	}
	
}

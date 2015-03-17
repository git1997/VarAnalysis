package edu.iastate.webslice.core;

import java.io.File;
import java.util.List;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.detection.ReferenceDetector;
import edu.iastate.analysis.references.detection.ReferenceManager;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class PrintDataFlows {
	
	private static String nodesFile = "/Users/HUNG/Desktop/nodes.txt";
	private static String edgesFile = "/Users/HUNG/Desktop/edges.txt";
	
	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		String projectPath = SubjectSystems.projectPath;
		List<String> entries = SubjectSystems.projectEntries;
		
		ReferenceManager referenceManager = new ReferenceManager();
		for (String entry : entries) {
			ReferenceManager refManager = new ReferenceDetector().detect(new File(projectPath, entry));
			referenceManager.getDataFlowManager().addDataFlows(refManager.getDataFlowManager());
		}
		
		StringBuilder str = new StringBuilder();
		for (Reference reference : referenceManager.getReferenceList()) {
			Position position = reference.getLocation().getStartPosition();
			str.append(reference.hashCode() + "\t"
					+ reference.getType() + "\t" + reference.getName() + "\t"
					+ position.getFileName() + "\t" + position.getLine() + "\t" + position.getOffset()
					+ System.lineSeparator());
		}
		FileIO.writeStringToFile(str.toString(), nodesFile);
		
		str = new StringBuilder();
		for (Reference ref1 : referenceManager.getReferenceList()) {
			Position pos1 = ref1.getLocation().getStartPosition();
			for (Reference ref2 : referenceManager.getDataFlowManager().getDataFlowFrom(ref1)) {
				Position pos2 = ref2.getLocation().getStartPosition();
				
				str.append(ref1.hashCode() + "\t"
						+ ref1.getType() + "\t" + ref1.getName() + "\t"
						+ pos1.getFileName() + "\t" + pos1.getLine() + "\t" + pos1.getOffset() + "\t"
						+ ref2.hashCode() + "\t"
						+ ref2.getType() + "\t" + ref2.getName() + "\t"
						+ pos2.getFileName() + "\t" + pos2.getLine() + "\t" + pos2.getOffset() + "\t"
						+ System.lineSeparator());
			}
		}
		FileIO.writeStringToFile(str.toString(), edgesFile);
	}
	
}

package util;

import java.util.HashMap;

/**
 * 
 * @author HUNG
 *
 */
public class Graphviz {

	public static String getStandardizedGraphvizLabel(String graphvizLabel) {
		String standardizedLabel = graphvizLabel;
		standardizedLabel = standardizedLabel.replace("\\r\\n", "\r\n");
		standardizedLabel = standardizedLabel.replace("\\n", "\n");
		
		HashMap<Character, String> mapTable = new HashMap<Character, String>();
		mapTable.put('\\', "\\\\");
		mapTable.put('"', "\\\"");
		mapTable.put('!', "\\!");
		mapTable.put('\r', "");
		mapTable.put('\n', "\\l");
		standardizedLabel = StringUtils.escape(standardizedLabel, mapTable);	
		
		if (standardizedLabel.contains("\\l"))
			standardizedLabel = standardizedLabel + "\\l";
		
		return "\"" + standardizedLabel + "\"";
	}
	
}

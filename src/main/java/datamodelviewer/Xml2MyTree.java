package datamodelviewer;

import datamodel.DataModel;
import datamodel.nodes.ext.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class Xml2MyTree {
	
	public static MyTree createTreeFromXml(String xmlFile) {
		DataNode rootNode = DataModel.readOutputFromXmlFile(xmlFile);
		return new MyTree(rootNode);
	}
	
}

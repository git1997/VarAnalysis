package edu.iastate.webtesting.evaluation;

import java.io.File;

import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.ReadWriteDataModelToFromXml;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.position.Range;

/**
 * 
 * @author HUNG
 *
 */
public class RunSymexForProject {
	
	/**
	 * The entry point of the program
	 */
	public static void main(String[] args) {
		DataNode rootNode = null;
		for (String entry : Config2.SUBJECT_SYSTEM_ENTRIES) {
			DataModel dataModel = new PhpExecuter().execute(new File(Config2.SUBJECT_SYSTEM_FOLDER + "/" + entry));
			if (rootNode == null)
				rootNode = dataModel.getRoot();
			else {
				Constraint constraint = ConstraintFactory.createAtomicConstraint(entry, Range.UNDEFINED);
				rootNode = DataNodeFactory.createCompactSelectNode(constraint, dataModel.getRoot(), rootNode);
			}
		}
		new ReadWriteDataModelToFromXml().writeDataModelToXmlFile(new DataModel(rootNode), Config2.DATA_MODEL_GENERATED_FILE);
	}
}
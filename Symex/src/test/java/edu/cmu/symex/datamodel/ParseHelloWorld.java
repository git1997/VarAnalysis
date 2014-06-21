package edu.cmu.symex.datamodel;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.WriteDataModelToIfDefs;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.run.RunSymexForFile;

public class ParseHelloWorld {

	@Test
	public void test() {
		DataModel model = new RunSymexForFile(new File("helloworld.php")).execute();
		assertNotNull(model);
		System.out.println(WriteDataModelToIfDefs.convert(model));

		model.getRoot().accept(new DataModelVisitor() {
			@Override
			public void visitLiteralNode(LiteralNode literalNode) {
				super.visitLiteralNode(literalNode);
				System.out.println(literalNode.getStringValue() + " @ "
						+ literalNode.getRegion());
			}
		});
	}

	
}

package edu.cmu.symex.datamodel;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.run.RunFile;

public class ParseHelloWorld {

	@Test
	public void test() {
		DataNode model = new RunFile(new File("helloworld.php"), new File("."))
				.run(null);
		assertNotNull(model);
		System.out.println(RunFile.valueToIfdefString(model, false));

		model.accept(new DataModelVisitor() {
			@Override
			public void visitLiteralNode(LiteralNode literalNode) {
				super.visitLiteralNode(literalNode);
				System.out.println(literalNode.getStringValue() + " @ "
						+ literalNode.getPositionRange());
			}
		});
	}

	
}

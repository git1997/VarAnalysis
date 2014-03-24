package datamodel;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import datamodel.nodes.DataModelVisitor;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import varanalysis.RunFile;

public class ParseHelloWorld {

	@Test
	public void test() {
		DataNode model = new RunFile(new File("helloworld.php"), new File("."))
				.run(null);
		assertNotNull(model);
		System.out.println(RunFile.valueToIfdefString(model, false));

		model.visit(new DataModelVisitor() {
			@Override
			public void visitLiteralNode(LiteralNode literalNode) {
				super.visitLiteralNode(literalNode);
				System.out.println(literalNode.getStringValue() + " @ "
						+ literalNode.getLocation());
			}
		});
	}

	
}

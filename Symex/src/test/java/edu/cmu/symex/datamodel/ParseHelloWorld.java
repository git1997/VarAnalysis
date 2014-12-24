package edu.cmu.symex.datamodel;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.run.RunSymexForFile;

public class ParseHelloWorld {

	@Test
	public void test() {
		DataModel model = new RunSymexForFile(new File("helloworld.php")).execute();
		assertNotNull(model);
		System.out.println(model.toIfdefString());

		model.getRoot().accept(new DataModelVisitor() {
			@Override
			public void visitLiteralNode(LiteralNode literalNode) {
				super.visitLiteralNode(literalNode);
				System.out.println(literalNode.getStringValue() + " @ "
						+ literalNode.getLocation());
			}
		});
	}

	
}
// helloworld.php
//<html>
//<head>
// <title>PHP Test</title>
//</head>
//<body>
//<?php if (isset($_GET["hi"])) echo '<p>Hello World</p>'; ?> 
//</body>
//</html>
package edu.cmu.va.varanalysis.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import util.FileIO;
import varanalysis.RunFile;
import datamodel.nodes.DataModelVisitor;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import de.fosd.typechef.parser.TokenReader;
import de.fosd.typechef.parser.common.CharacterToken;
import errormodel.SymExErrorHandler;
import errormodel.SymExException;

public class FileProcessorTest {

	private static final String FILENAME = "helloworld.php";
	private static final File FILE = new File(FILENAME);

	private DataNode symExFile() {
		DataNode model = new FileProcessor().executeSymbolically(
				new SymExErrorHandler() {

					@Override
					public void warning(SymExException exception) {
						fail("warning occurred" + exception);
						exception.printStackTrace();
					}

					@Override
					public void fatalError(SymExException exception) {
						fail("fatal error occurred" + exception);
						exception.printStackTrace();
					}

					@Override
					public void error(SymExException exception) {
						fail("error occurred" + exception);
						exception.printStackTrace();

					}
				}, FILE);
		assertNotNull(model);
		return model;
	}

	private char[] readFile() {
		return FileIO.readStringFromFile(FILE).toCharArray();
	}

	@Test
	public void testSymEx() {
		DataNode model = symExFile();

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

	@Test
	public void testLex() {
		DataNode model = symExFile();

		TokenReader<CharacterToken, Object> tokenStream = new FileProcessor()
				.lexDModel(model);

		System.out.println(tokenStream);
		
		char[] sourceFile = readFile();
		while (!tokenStream.atEnd()) {
			CharacterToken t = tokenStream.first();
			tokenStream=tokenStream.rest();
			
			int pos = t.getPosition().getColumn();
			assertTrue(t.getPosition().getFile().contains(FILENAME));
			assertTrue(pos<sourceFile.length);
			assertEquals(sourceFile[pos], t.getKind());
		}

	}


	// TODO write test for includes
}

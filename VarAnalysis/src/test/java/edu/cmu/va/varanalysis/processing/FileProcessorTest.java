package edu.cmu.va.varanalysis.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import scala.collection.immutable.List;
import de.fosd.typechef.parser.TokenReader;
import de.fosd.typechef.parser.common.CharacterToken;
import de.fosd.typechef.parser.html.HElementToken;
import de.fosd.typechef.parser.html.VarDom;
import edu.iastate.symex.datamodel.DataModelVisitor;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.cmu.va.varanalysis.model.*;
import edu.iastate.symex.errormodel.SymexErrorHandler;
import edu.iastate.symex.errormodel.SymexException;
import edu.iastate.symex.run.RunSymexForFile;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.util.FileIO;

public class FileProcessorTest {

	private static final String FILENAME = "helloworld.php";
	private static final File FILE = new File(FILENAME);

	private DataNode symExFile() {
		DataNode model = new FileProcessor().executeSymbolically(
				failOnErrorReporter, FILE);
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
		System.out.println(new DataModel(model).toIfdefString());

		model.accept(new DataModelVisitor() {
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
			tokenStream = tokenStream.rest();

			int pos = t.getPosition().getColumn();
			assertTrue(t.getPosition().getFile().contains(FILENAME));
			assertTrue(pos < sourceFile.length);
			assertEquals(sourceFile[pos], t.getKind());
		}

	}

	private final FileProcessor p = new FileProcessor();
	private SymexErrorHandler failOnErrorReporter = new SymexErrorHandler() {

		@Override
		public void warning(SymexException exception) {
			fail("warning occurred: " + exception);
			exception.printStackTrace();
		}

		@Override
		public void fatalError(SymexException exception) {
			fail("fatal error occurred: " + exception);
			exception.printStackTrace();
		}

		@Override
		public void error(SymexException exception) {
			fail("error occurred: " + exception);
			exception.printStackTrace();

		}
	};

	@Test
	public void testSAX() {
		List<HElementToken> result = p.parseSAX(p.lexDModel(symExFile()),
				failOnErrorReporter);
		System.out.println(result);
	}

	@Test
	public void testDOM() {
		VarDom result =getVarDom();
		System.out.println(result);
	}

	private VarDom getVarDom() {
		 return p.parseDOM(
				p.parseSAX(p.lexDModel(symExFile()), failOnErrorReporter),
				failOnErrorReporter);
	}
	
	@Test
	public void testHTMLCallGraph() {
		VarDom vardom = getVarDom();
		CallGraph cg=p.getHTMLCallGraph(vardom);
		System.out.println(cg);
	}

	

	// TODO write test for includes
}

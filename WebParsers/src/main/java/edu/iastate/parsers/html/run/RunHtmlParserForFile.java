package edu.iastate.parsers.html.run;

import java.io.File;

import edu.iastate.parsers.html.core.ParseDataModel;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * 
 * @author HUNG
 *
 */
public class RunHtmlParserForFile {
	
	/**
	 * PHP file to test
	 */	
	public static String PHP_FILE = "/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/index.php";

	/**
	 * PHP file to be executed
	 */
	private File file;

	/**
	 * The entry point of the program.
	 */
	public static void main(String[] args) {
		MyLogger.setLevel(MyLevel.ALL);
		//MyLogger.addOutputType(OutputType.File);
		
		HtmlDocument htmlDocument = new RunHtmlParserForFile(new File(PHP_FILE)).execute();
		
		MyLogger.log(MyLevel.INFO, htmlDocument.toIfdefString());
		
		//MyLogger.writeLogMessagesToFile(new File("/Users/HUNG/Desktop/output.txt"));
	}
	
	/**
	 * Constructor.
	 * @param file The PHP file to be executed
	 */
	public RunHtmlParserForFile(File file) {
		this.file = file;
	}
	
	/**
	 * Executes the file.
	 */
	public HtmlDocument execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[RunHtmlParserForFile:" + file + "] Started.");
		
		DataModel dataModel = new PhpExecuter().execute(file);
		HtmlDocument htmlDocument = new ParseDataModel().parse(dataModel);
		
		MyLogger.log(MyLevel.PROGRESS, "[RunHtmlParserForFile:" + file + "] Done in " + timer.getElapsedSecondsInText() + ".");
		return htmlDocument;
	}
		
}

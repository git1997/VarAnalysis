package edu.iastate.symex.run;

import java.io.File;

import edu.iastate.symex.util.FileIO;
import edu.iastate.symex.util.StringUtils;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.symex.datamodel.nodes.ArrayNode;
import edu.iastate.symex.datamodel.nodes.ConcatNode;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.LiteralNode;
import edu.iastate.symex.datamodel.nodes.ObjectNode;
import edu.iastate.symex.datamodel.nodes.RepeatNode;
import edu.iastate.symex.datamodel.nodes.SelectNode;
import edu.iastate.symex.datamodel.nodes.SymbolicNode;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.errormodel.SymexErrorHandler;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.AtomicPositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class RunFile {

	private File file;
	private File workingDirectory;
	
	
	public RunFile(File file, File workingDirectory/*, String outputFolder*/) {
		this.file = file;
		this.workingDirectory = workingDirectory;
//		this.outputFolder = outputFolder;
	}
	
	public DataNode run(SymexErrorHandler reporter) {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" +  StringUtils.getFileSystemSlash() + workingDirectory + "] Started.");
		
		RunSymexForFile createDataModelForFile = new RunSymexForFile(file, workingDirectory);
		DataModel dataModel = createDataModelForFile.execute();
		DataNode outputDataNode = dataModel.getRoot();
		
//		dataModel.printOutputToXmlFile(outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile);
//		
//		String ifdefOutput = valueToIfdefString(outputDataNode, false);
//		FileIO.writeStringToFile(ifdefOutput, outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile.replace("xml", "txt"));
//		
//		String ifdefOutputWithMapping = valueToIfdefString(outputDataNode, true);
//		FileIO.writeStringToFile(ifdefOutputWithMapping, outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile.replace(".xml", "_withmapping.txt"));
//		
//		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Done in " + timer.getElapsedSecondsInText() + ".");
		return outputDataNode;
	}
	
	public static String valueToIfdefString(DataNode dataNode, boolean withLocationInfo) {
			return "";
    }

}

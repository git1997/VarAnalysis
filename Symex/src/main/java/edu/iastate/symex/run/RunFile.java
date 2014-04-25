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
		if (dataNode instanceof ArrayNode) {
			return "[Array]";
		}
		
		else if (dataNode instanceof ObjectNode) {
			return "[Object]";
		}
		
		else if (dataNode instanceof ConcatNode) {
	    	StringBuilder str = new StringBuilder();
	    	for (DataNode child : ((ConcatNode) dataNode).getChildNodes()) {
	    		String childValue = valueToIfdefString(child, withLocationInfo);
    			str.append(childValue);
    		}
    		return str.toString();
		}
		
		else if (dataNode instanceof LiteralNode) {
			String stringValue = ((LiteralNode) dataNode).getStringValue();
			PositionRange positionRange = ((LiteralNode) dataNode).getPositionRange();
			String locationInfo = "[Unresolved Location]";
			if (positionRange instanceof AtomicPositionRange) {
				AtomicPositionRange p = (AtomicPositionRange) positionRange;
				locationInfo = " (Location: " + p.getFile().getPath() + " @ Line " + FileIO.getLineFromOffsetInFile(p.getFile(), p.getOffset()) + ") ";
			}

			return stringValue + (withLocationInfo ? locationInfo : "");
		}
		
		else if (dataNode instanceof RepeatNode) {
			return "[RepeatBegin]\n" + valueToIfdefString(((RepeatNode) dataNode).getChildNode(), withLocationInfo) + "\n[RepeatEnd]"; 
		}
		
		else if (dataNode instanceof SelectNode) {
			String constraint = ((SelectNode) dataNode).getConditionString() != null ? ((SelectNode) dataNode).getConditionString().getStringValue() : "[Unresolved Constraint]";
			
			String trueBranch = valueToIfdefString(((SelectNode) dataNode).getNodeInTrueBranch(), withLocationInfo);
			String falseBranch = valueToIfdefString(((SelectNode) dataNode).getNodeInFalseBranch(), withLocationInfo);
		
			String retString = "\n#if (" + constraint + ")\n"
					+ trueBranch + "\n"
					+ "#else" + "\n"
					+ falseBranch + "\n"
					+ "#endif" + "\n";
			
			return retString;
		}
		
		else if (dataNode instanceof SymbolicNode) {
			//return "[Symbolic:" + ((SymbolicNode) dataNode).getPhpNode().getStringValue() + "]";
			return "[SYM]";
		}
		
	    return ""; // Should not reach here
    }

}

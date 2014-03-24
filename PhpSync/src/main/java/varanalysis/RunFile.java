package varanalysis;

import java.io.File;

import main.CreateDataModelForFile;
import util.FileIO;
import util.StringUtils;
import util.Timer;
import util.logging.MyLevel;
import util.logging.MyLogger;
import util.sourcetracing.SourceCodeLocation;
import util.sourcetracing.UndefinedLocation;
import config.DataModelConfig;
import datamodel.DataModel;
import datamodel.nodes.ArrayNode;
import datamodel.nodes.ConcatNode;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import datamodel.nodes.ObjectNode;
import datamodel.nodes.RepeatNode;
import datamodel.nodes.SelectNode;
import datamodel.nodes.SymbolicNode;
import errormodel.SymExErrorHandler;

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
	
	public DataNode run(SymExErrorHandler reporter) {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" +  StringUtils.getFileSystemSlash() + workingDirectory + "] Started.");
		
		CreateDataModelForFile createDataModelForFile = new CreateDataModelForFile(file, workingDirectory);
		DataModel dataModel = createDataModelForFile.execute();
		DataNode outputDataNode = dataModel.getOutputDataNode();
		
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
			String stringValue = ((LiteralNode) dataNode).getUnescapedStringValue();
			SourceCodeLocation location = ((LiteralNode) dataNode).getLocation().getLocationAtOffset(0);
			String locationInfo = "[Unresolved Location]";
			if (!(location instanceof UndefinedLocation)) {
				SourceCodeLocation absoluteLocation = new SourceCodeLocation(location.getFilePath(), location.getPosition());
				locationInfo = " (Location: " + location.getFilePath() + " @ Line " + absoluteLocation.getLine() + ") ";
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

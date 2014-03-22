package varanalysis;

import logging.MyLevel;
import logging.MyLogger;
import main.CreateDataModelForFile;
import sourcetracing.SourceCodeLocation;
import sourcetracing.UndefinedLocation;
import util.FileIO;
import util.StringUtils;
import util.Timer;
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

/**
 * 
 * @author HUNG
 *
 */
public class RunFile {

//	private static String PROJECT_NAME 			= "addressbookv6.2.12";
//	private static String RELATIVE_FILE_PATH 	= "map.php";
	
//	private static String PROJECT_NAME 			= "addressbookv6.2.12";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
	
//	private static String PROJECT_NAME 			= "Base-1.4.5";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
	
//	private static String PROJECT_NAME 			= "beehiveforum101";
//	private static String RELATIVE_FILE_PATH 	= "forum/index.php";
//	
//	private static String PROJECT_NAME 			= "BlogPHPv2";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
//	
//	private static String PROJECT_NAME 			= "Devana-1.6.6";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
//	
//	private static String PROJECT_NAME 			= "linpha-1.3.4";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
	
//	private static String PROJECT_NAME 			= "manhali-1.9.2";
//	private static String RELATIVE_FILE_PATH 	= "index.php";

//	private static String PROJECT_NAME 			= "Mrbs-1.4.8";
//	private static String RELATIVE_FILE_PATH 	= "web/index.php";
	
//	private static String PROJECT_NAME 			= "PHP-Nuke-8.1";
//	private static String RELATIVE_FILE_PATH 	= "html/index.php";
	
//	private static String PROJECT_NAME 			= "phpMyAdmin-3.3.10-english";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
	
//	private static String PROJECT_NAME 			= "pivotx_2.3.0";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
	
//	private static String PROJECT_NAME 			= "SchoolMate-1.5.4";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
//	
//	private static String PROJECT_NAME 			= "SQLiteManager-1.2.4";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
//	
//	private static String PROJECT_NAME 			= "SquirrelMail-1.4.22";
//	private static String RELATIVE_FILE_PATH 	= "src/login.php";
//	
//	private static String PROJECT_NAME 			= "WordPress-3.4.2";
//	private static String RELATIVE_FILE_PATH 	= "wp-login.php";
	
//	private static String PROJECT_NAME 			= "TimeClock-1.04";
//	private static String RELATIVE_FILE_PATH 	= "timeclock.php"; //"css/default.php";//"timeclock.php";
//	
//	private static String PROJECT_NAME 			= "UPB-2.2.7";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
	
	private static String PROJECT_NAME 			= "webchess-1.0.0";
	private static String RELATIVE_FILE_PATH 	= "index.php";
	
	public static String PROJECT_FOLDER 		= "/Work/To-do/Data/Web Projects/Server Code/" + PROJECT_NAME;
	public static String OUTPUT_FOLDER 			= "/Work/To-do/JSVarex Project/Study/Output/" + PROJECT_NAME;

	private String projectFolder;
	private String relativeFilePath;
	private String outputFolder;
	
	public static void main(String[] args) {
		//FileIO.cleanFolder(OUTPUT_FOLDER);
		new RunFile(PROJECT_FOLDER, RELATIVE_FILE_PATH, OUTPUT_FOLDER).run();
	}
	
	public RunFile(String projectFolder, String relaltiveFilePath, String outputFolder) {
		this.projectFolder = projectFolder;
		this.relativeFilePath = relaltiveFilePath;
		this.outputFolder = outputFolder;
	}
	
	public void run() {
		String projectName = projectFolder.substring(projectFolder.lastIndexOf(StringUtils.getFileSystemSlash() ) + 1);
		
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Started.");
		
		CreateDataModelForFile createDataModelForFile = new CreateDataModelForFile(projectFolder, relativeFilePath, null, DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT);
		DataModel dataModel = createDataModelForFile.execute();
		DataNode outputDataNode = dataModel.getOutputDataNode();
		
		dataModel.printOutputToXmlFile(outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile);
		
		String ifdefOutput = valueToIfdefString(outputDataNode, false);
		FileIO.writeStringToFile(ifdefOutput, outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile.replace("xml", "txt"));
		
		String ifdefOutputWithMapping = valueToIfdefString(outputDataNode, true);
		FileIO.writeStringToFile(ifdefOutputWithMapping, outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile.replace(".xml", "_withmapping.txt"));
		
		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Done in " + timer.getElapsedSecondsInText() + ".");
	}
	
	private static String valueToIfdefString(DataNode dataNode, boolean withLocationInfo) {
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
				SourceCodeLocation absoluteLocation = new SourceCodeLocation(RunFile.PROJECT_FOLDER + StringUtils.getFileSystemSlash() + location.getFilePath(), location.getPosition());
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

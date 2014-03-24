//package varanalysis;
//
//import main.CreateDataModelForFile;
//import util.FileIO;
//import util.StringUtils;
//import util.Timer;
//import util.logging.MyLevel;
//import util.logging.MyLogger;
//import util.sourcetracing.SourceCodeLocation;
//import util.sourcetracing.UndefinedLocation;
//import config.DataModelConfig;
//import datamodel.DataModel;
//import datamodel.nodes.ArrayNode;
//import datamodel.nodes.ConcatNode;
//import datamodel.nodes.DataNode;
//import datamodel.nodes.LiteralNode;
//import datamodel.nodes.ObjectNode;
//import datamodel.nodes.RepeatNode;
//import datamodel.nodes.SelectNode;
//import datamodel.nodes.SymbolicNode;
//
///**
// * 
// * @author HUNG
// *
// */
//public class RunFileForEvaluation {
//
//	private static String PROJECT_NAME 			= "addressbookv6.2.12";
//	private static String RELATIVE_FILE_PATH 	= "index.php";
//	
////	private static String PROJECT_NAME 			= "SchoolMate-1.5.4";
////	private static String RELATIVE_FILE_PATH 	= "index.php";
//	
////	private static String PROJECT_NAME 			= "TimeClock-1.04";
////	private static String RELATIVE_FILE_PATH 	= "timeclock.php"; //"css/default.php";//"timeclock.php";
//
////	private static String PROJECT_NAME 			= "UPB-2.2.7";
////	private static String RELATIVE_FILE_PATH 	= "admin_forums.php";
//	
////	private static String PROJECT_NAME 			= "webchess-1.0.0";
////	private static String RELATIVE_FILE_PATH 	= "index.php";	
//	
//	public static String PROJECT_FOLDER 		= "/Work/To-do/Data/Web Projects/Server Code/" + PROJECT_NAME;
//	public static String OUTPUT_FOLDER 			= "/Work/To-do/JSVarex Project/Study/Output/" + PROJECT_NAME;
//
//	private String projectFolder;
//	private String relativeFilePath;
//	private String outputFolder;
//	
//	public static void main(String[] args) {
//		//FileIO.cleanFolder(OUTPUT_FOLDER);
//		new RunFileForEvaluation(PROJECT_FOLDER, RELATIVE_FILE_PATH, OUTPUT_FOLDER).run();
//	}
//	
//	public RunFileForEvaluation(String projectFolder, String relaltiveFilePath, String outputFolder) {
//		this.projectFolder = projectFolder;
//		this.relativeFilePath = relaltiveFilePath;
//		this.outputFolder = outputFolder;
//	}
//	
//	public void run() {
//		String projectName = projectFolder.substring(projectFolder.lastIndexOf(StringUtils.getFileSystemSlash() ) + 1);
//		
//		Timer timer = new Timer();
//		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Started.");
//		Evaluation.start();
//		
//		CreateDataModelForFile createDataModelForFile = new CreateDataModelForFile(projectFolder, relativeFilePath, null, DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT);
//		DataModel dataModel = createDataModelForFile.execute();
//		DataNode outputDataNode = dataModel.getOutputDataNode();
//		
//		dataModel.printOutputToXmlFile(outputFolder + StringUtils.getFileSystemSlash() + DataModelConfig.dataModelXmlFile);
//		Evaluation.dataModelCreated(dataModel);
//		Evaluation.dataModelCreated2(dataModel);
//		
//		Evaluation.finish();
//		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Done in " + timer.getElapsedSecondsInText() + ".");
//	}
//	
//	private static String valueToIfdefString(DataNode dataNode, boolean withLocationInfo) {
//		if (dataNode instanceof ArrayNode) {
//			return "[Array]";
//		}
//		
//		else if (dataNode instanceof ObjectNode) {
//			return "[Object]";
//		}
//		
//		else if (dataNode instanceof ConcatNode) {
//	    	StringBuilder str = new StringBuilder();
//	    	for (DataNode child : ((ConcatNode) dataNode).getChildNodes()) {
//	    		String childValue = valueToIfdefString(child, withLocationInfo);
//    			str.append(childValue);
//    		}
//    		return str.toString();
//		}
//		
//		else if (dataNode instanceof LiteralNode) {
//			String stringValue = ((LiteralNode) dataNode).getUnescapedStringValue();
//			SourceCodeLocation location = ((LiteralNode) dataNode).getLocation().getLocationAtOffset(0);
//			String locationInfo = "[Unresolved Location]";
//			if (!(location instanceof UndefinedLocation)) {
//				SourceCodeLocation absoluteLocation = new SourceCodeLocation(RunFileForEvaluation.PROJECT_FOLDER + StringUtils.getFileSystemSlash() + location.getFilePath(), location.getPosition());
//				locationInfo = " (Location: " + location.getFilePath() + " @ Line " + absoluteLocation.getLine() + ") ";
//			}
//			
//			return stringValue + (withLocationInfo ? locationInfo : "");
//		}
//		
//		else if (dataNode instanceof RepeatNode) {
//			return "[RepeatBegin]\n" + valueToIfdefString(((RepeatNode) dataNode).getChildNode(), withLocationInfo) + "\n[RepeatEnd]"; 
//		}
//		
//		else if (dataNode instanceof SelectNode) {
//			String constraint = ((SelectNode) dataNode).getConditionString() != null ? ((SelectNode) dataNode).getConditionString().getStringValue() : "[Unresolved Constraint]";
//			
//			String trueBranch = valueToIfdefString(((SelectNode) dataNode).getNodeInTrueBranch(), withLocationInfo);
//			String falseBranch = valueToIfdefString(((SelectNode) dataNode).getNodeInFalseBranch(), withLocationInfo);
//		
//			String retString = "\n#if (" + constraint + ")\n"
//					+ trueBranch + "\n"
//					+ "#else" + "\n"
//					+ falseBranch + "\n"
//					+ "#endif" + "\n";
//			
//			return retString;
//		}
//		
//		else if (dataNode instanceof SymbolicNode) {
//			//return "[Symbolic:" + ((SymbolicNode) dataNode).getPhpNode().getStringValue() + "]";
//			return "[SYM]";
//		}
//		
//	    return ""; // Should not reach here
//    }
//
//}

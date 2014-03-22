package varanalysis;

import java.util.HashMap;
import logging.MyLevel;
import logging.MyLogger;
import main.CreateDataModelForFile;
import config.DataModelConfig;
import datamodel.DataModel;
import datamodel.nodes.DataNode;
import datamodel.nodes.LiteralNode;
import util.FileIO;
import util.StringUtils;
import util.Timer;

/**
 * 
 * @author HUNG
 *
 */
public class CountOutputCharsInProjectWithSymExec {
	
	public static String PROJECT_NAME 	= 
//			"addressbookv6.2.12";
//			"SchoolMate-1.5.4";
			"TimeClock-1.04";
//			"UPB-2.2.7";
//			"webchess-1.0.0";

	private static String projectFolder = "/Work/To-do/Data/Web Projects/Server Code/" + PROJECT_NAME;
	
	public static void main(String[] args) {
		HashMap<String, LiteralNode> mapLocationToStringValue = new HashMap<String, LiteralNode>();
		for (String file : FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php"})) {
//			if (file.contains("admin"))
//				continue;
			
			countOutputCharsInFile(file, mapLocationToStringValue);
		}
		
		int numOutputChars = Evaluation.countCharsFromStrings(mapLocationToStringValue.values());
		
		System.out.println("Number of output chars (with symbolic execution): " + numOutputChars);
	}

	public static void countOutputCharsInFile(String file, HashMap<String, LiteralNode> map) {
		String projectName = projectFolder.substring(projectFolder.lastIndexOf(StringUtils.getFileSystemSlash() ) + 1);
		String relativeFilePath = file.substring(projectFolder.length() + 1);
		
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Started.");
		
		CreateDataModelForFile createDataModelForFile = new CreateDataModelForFile(projectFolder, relativeFilePath, null, DataModelConfig.PRINT_DATA_MODEL_AS_OBJECT);
		DataModel dataModel = createDataModelForFile.execute();
		DataNode outputDataNode = dataModel.getOutputDataNode();
		Evaluation.getOutputChars(outputDataNode, map);
		
		MyLogger.log(MyLevel.PROGRESS, "[RunFile:" + projectName + StringUtils.getFileSystemSlash() + relativeFilePath + "] Done in " + timer.getElapsedSecondsInText() + ".");
	}
	
}

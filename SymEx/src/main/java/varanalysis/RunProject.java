//package varanalysis;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//import util.FileIO;
//import util.Timer;
//import util.logging.MyLevel;
//import util.logging.MyLogger;
//
///**
// * 
// * @author HUNG
// *
// */
//public class RunProject {
//	
//	public static String PROJECT_NAME 	= "SquirrelMail-1.4.22"; //"SquirrelMail-1.4.22"; //"PostfixAdmin-2.3.5"; //"SquirrelMail-1.4.22"; // "Mrbs-1.4.1"; //"addressbookv6.2.12"; //"beehiveforum101"; //"Base-1.4.5"; //"Mrbs-1.4.1"; //PhpDocumentor-1.4.4"; //"PostfixAdmin-2.3.5"; //"SchoolMate-1.5.4"; //"SquirrelMail-1.4.22";
//	
//	private static String projectFolder = "C:\\Users\\HUNG\\Desktop\\Lab\\Web Projects\\workspace\\Server Code\\" + PROJECT_NAME;
//	
//	private static String outputFolder = "C:\\Users\\HUNG\\Desktop\\Temp";
//	
//	/**
//	 * The entry point of the program.
//	 */
//	public static void main(String[] args) {
//		runProject(projectFolder);
//	}
//	
//	private static void runProject(String projectFolder) {
//		Timer timer = new Timer();
//		MyLogger.log(MyLevel.PROGRESS, "[RunProject:" + projectFolder.substring(projectFolder.lastIndexOf("\\") + 1) + "] Started.");
//
//		ArrayList<String> phpFiles = FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php"}); // TODO: Add .html, .js
//		Collections.sort(phpFiles, new Comparator<String>()  {
//			@Override
//            public int compare(String str1, String str2) {
//                return str1.length() - str2.length();
//            }        
//        }); // Sort PHP files based on length
//		
//		for (String absoluteFilePath : phpFiles) {
//			String relativeFilePath = absoluteFilePath.substring(projectFolder.length() + 1);
//		
//			new RunFile(projectFolder, relativeFilePath, outputFolder).run();
//		}
//		
//		MyLogger.log(MyLevel.PROGRESS, "[RunProject:" + projectFolder.substring(projectFolder.lastIndexOf("\\") + 1) + "] Done in " + timer.getElapsedSecondsInText() + ".");
//
//	}
//	
//}

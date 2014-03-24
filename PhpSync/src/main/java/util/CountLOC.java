package util;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class CountLOC {
	
	public static String projectName	= "webchess-1.0.0";
	public static String projectFolder	= "/Work/To-do/Data/Web Projects/Server Code/" + projectName;

	public static void main(String[] args) {
		ArrayList<String> allFiles = FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php", ".html", ".js"});
		int count = 0;
		
		for (String file : allFiles) {
			count += getLOC(new File(file));
		}
		
		System.out.println("Project folder:  " + projectFolder);
		System.out.println("Number of files: " + allFiles.size());
		System.out.println("Total LOC:       " + count);

	}
	
	public static int getLOC(File file) {
		String fileContent = FileIO.readStringFromFile(file);
		int count = 1;
		for (int i = 0; i < fileContent.length(); i++)
			if (fileContent.charAt(i) == '\n')
				count++;
		return count;
	}

}

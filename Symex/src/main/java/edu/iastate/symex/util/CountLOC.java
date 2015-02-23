package edu.iastate.symex.util;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author HUNG
 *
 */
public class CountLOC {
	
	public static String projectName	= "AddressBook-6.2.12";
	public static String projectFolder	= "/Work/Data/Web Projects/Server Code/" + projectName;

	public static void main(String[] args) {
		ArrayList<String> allFiles = FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php", ".html", ".js"});

		int totalLOC = 0;
		for (String file : allFiles) {
			totalLOC += FileIO.getLinesOfCodeInFile(new File(file));
		}
		
		System.out.println("Project folder:  " + projectFolder);
		System.out.println("Number of files: " + allFiles.size());
		System.out.println("Total LOC:       " + totalLOC);
	}
	
}

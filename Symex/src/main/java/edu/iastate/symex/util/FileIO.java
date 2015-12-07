package edu.iastate.symex.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author HUNG
 * 
 */
public class FileIO {
	
	// Cache the files' contents for faster reads
	public static boolean TURN_CACHE_ON = true;
	private static HashMap<File, String> fileMap = new HashMap<File, String>();

	/*
	 * Read a string from a file
	 */
	
	public static String readStringFromFile(String inputFile) {
		return readStringFromFile(new File(inputFile));
	}

	public static String readStringFromFile(File inputFile) {
		if (TURN_CACHE_ON) {
			if (!fileMap.containsKey(inputFile))
				fileMap.put(inputFile, readStringFromFileWithoutCache(inputFile));
			return fileMap.get(inputFile);
		}
		else {
			return readStringFromFileWithoutCache(inputFile);
		}
	}
	
	private static String readStringFromFileWithoutCache(File inputFile) {
		try {
			return readStringFromStream(new BufferedInputStream(new FileInputStream(inputFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String[] readLinesFromFile(String inputFile) {
		String fileContent = readStringFromFile(inputFile);
		return fileContent.split("\r?\n");
	}

	public static String readStringFromStream(InputStream is) {
		try {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			Reader reader = new BufferedReader(new InputStreamReader(is));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			reader.close();
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/*
	 * Write a string to a file
	 */
	
	public static void writeStringToFile(String string, String outputFile) {
		writeStringToFile(string, new File(outputFile));
	}

	public static void writeStringToFile(String string, File outputFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(string);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Read/write an object
	 */

	public static Object readObjectFromFile(String objectFile) {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(objectFile)));
			Object object = in.readObject();
			in.close();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void writeObjectToFile(Object object, String objectFile) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(objectFile)));
			out.writeObject(object);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Get all the files in a folder
	 */

	public static ArrayList<String> getAllFilesInFolder(String folder) {
		ArrayList<String> allFiles = new ArrayList<String>();
		for (File file : new File(folder).listFiles()) {
			if (file.isFile())
				allFiles.add(file.getPath());
			else
				allFiles.addAll(getAllFilesInFolder(file.getPath()));
		}
		return allFiles;
	}

	public static ArrayList<String> getAllFilesInFolderByExtensions(String folder, String[] extensions) {
		ArrayList<String> allFiles = new ArrayList<String>();
		for (String file : getAllFilesInFolder(folder)) {
			String extension = (file.lastIndexOf('.') != -1 ? file.substring(file.lastIndexOf('.')) : "");
			for (int i = 0; i < extensions.length; i++) {
				if (extensions[i].equals(extension)) {
					allFiles.add(file);
					break;
				}
			}
		}
		return allFiles;
	}

	/**
	 * Resolves a file path included from a certain file in a projectFolder
	 * 
	 * @param projectFolder
	 * @param currentRelativeFilePath
	 * @param includedFilePath The included file path before being resolved (e.g. it can be '../file.txt')
	 * @return The included file path relative to the projectFolder after being resolved (e.g. 'folder/file.txt')
	 */
	public static String resolveIncludedFilePath(String projectFolder, String currentRelativeFilePath, String includedFilePath) {
		// TODO Revise this code
		File projectPath = new File(projectFolder);
		File path = new File(projectFolder + "\\" + currentRelativeFilePath);

		File includedFile = null;
		while (path != null && path.compareTo(projectPath) != 0) {
			includedFile = new File(path.getParent(), includedFilePath);
			if (includedFile.isFile())
				break;
			includedFile = null;
			path = path.getParentFile();
		}

		String includedFileAbsolutePath = null;
		if (includedFile != null) {
			try {
				//getCanonicalPath() is better than getAbsolutePath() 
				//because it cancels the recursive ..\something\..\something\..
				includedFileAbsolutePath = includedFile.getCanonicalPath(); 
			} catch (IOException e) {
			}
		}

		if (includedFileAbsolutePath != null) {
			String standardizedProjectPath = projectFolder.replace('/', '\\'); // Standardize the file path to Windows format 
			String standardizedIncludedPath = includedFileAbsolutePath.replace('/', '\\'); // Standardize the file path to Windows format
			if (standardizedIncludedPath.startsWith(standardizedProjectPath))
				return standardizedIncludedPath.substring(standardizedProjectPath.length() + 1);
		}
		return null;
	}

	/*
	 * Copy files & folders
	 */

	public static void copyFileOrFolderIfNotExist(String sourceFile, String targetFile) {
		if (!new File(targetFile).exists())
			copyFileOrFolder(sourceFile, targetFile);
	}

	public static void copyFileOrFolder(String sourceFile, String targetFile) {
		File sourceLocation = new File(sourceFile);
		File targetLocation = new File(targetFile);
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyFileOrFolder(sourceLocation + "/" + children[i],
						targetLocation + "/" + children[i]);
			}
		} else {
			if (sourceLocation.lastModified() != targetLocation.lastModified()) {
				try {
					InputStream in = new FileInputStream(sourceLocation);
					OutputStream out = new FileOutputStream(targetLocation);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				targetLocation.setLastModified(sourceLocation.lastModified());
			}
		}
	}

	/*
	 * Delete files & folders
	 */

	public static void cleanFolder(String folderPath) {
		deleteFileRecursive(folderPath);
		new File(folderPath).mkdirs();
	}

	public static void cleanFile(String filePath) {
		deleteFileRecursive(filePath);
		new File(filePath).getParentFile().mkdirs();
	}

	public static void deleteFileRecursive(String filePath) {
		File[] files = new File(filePath).listFiles();
		if (files == null)
			return;
		for (File file : files) {
			if (file.isDirectory())
				deleteFileRecursive(file.getPath());
			file.delete();
		}
	}

	/*
	 * Handling LOC (lines of code)
	 */

	/**
	 * Returns the number of lines of code in a file.
	 */
	public static int getLinesOfCodeInFile(File file) {
		String fileContent = FileIO.readStringFromFile(file);
		int line = 1;
		for (int i = 0; i < fileContent.length(); i++)
			if (fileContent.charAt(i) == '\n')
				line++;
		return line;
	}
	
	/**
	 * Returns the line number of an offset position in a file.
	 * Line starts from 1.
	 */
	public static int getLineFromOffsetInFile(File file, int offset) {
		String fileContent = FileIO.readStringFromFile(file);
		int line = 1;
		for (int i = 0; i < offset; i++)
			if (fileContent.charAt(i) == '\n')
				line++;
		return line;
	}

}
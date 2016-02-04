package edu.iastate.webtesting.util_clone;

import edu.iastate.symex.util.FileIO;

/**
 * 
 * @author HUNG
 *
 */
public class CodeLocation {
	public static final CodeLocation UNDEFINED = new CodeLocation("", 0, -1);
	
	private String file;
	private int line;
	private int offset;
	
	public CodeLocation(String file, int line, int offset) {
		this.file = file;
		this.line = line;
		this.offset = offset;
	}
	
	public String getFile() {
		return file;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public static String getShortPath(String absolutePath) {
		if (absolutePath != null)
			return absolutePath.substring(absolutePath.indexOf("/", absolutePath.indexOf("/WebApps/") + "/WebApps/".length()) + 1);
		else
			return "";
	}
	
	/**
	 * Tries to resolve offset since Quercus only provides line numbers.
	 */
	public void resolveOffset(String searchValue) {
		String fileContent = FileIO.readStringFromFile(file);
		int offset_ = 0;
		int line_ = 1;
		while (line_ < line) {
			if (fileContent.charAt(offset_) == '\n')
				line_++;
			offset_++;
		}
		
		this.offset = fileContent.indexOf(searchValue, offset_);
		
		// ADHOC: Address the case when fileContent contains \" which evaluates to "
		if (this.offset == -1 && searchValue.indexOf('"') != -1) {
			searchValue = searchValue.substring(0, searchValue.indexOf('"')); 
			this.offset = fileContent.indexOf(searchValue, offset_);
		}
		// ADHOC: Address the case when fileContent contains \n which evaluates to NewLine
		if (this.offset == -1 && searchValue.indexOf('\n') != -1) {
			searchValue = searchValue.substring(0, searchValue.indexOf('\n'));
			if (searchValue.indexOf('\r') != -1)
				searchValue = searchValue.substring(0, searchValue.indexOf('\r'));
			this.offset = fileContent.indexOf(searchValue, offset_);
		}
		
		if (this.offset == -1)
			System.out.println("In ColdLocation.java: Cannot resolve offset of " + searchValue);
		else if (fileContent.indexOf(searchValue, this.offset + 1) != -1)
			System.out.println("In CodeLocation.java: Ambiguity when resolving offset for: " + searchValue);
	}
}

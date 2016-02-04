package edu.iastate.webtesting.bugcoverage;

import edu.iastate.symex.util.FileIO;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author HUNG
 *
 */
public class SpellChecker {

	private static final String DICTIONARY_FILE = "src/main/resources/files/dictionary-jazzy.txt"; // From http://sourceforge.net/projects/jazzy/

	private static Set<String> dictionary = new HashSet<String>();

	static {
		String dictionaryContent = FileIO.readStringFromFile(DICTIONARY_FILE);
		String[] words = dictionaryContent.split("\\s+");
		for (String word : words) {
			dictionary.add(word.toLowerCase());
		}
	}

	public static boolean hasSpellingError(String word) {
		word = word.replace("&nbsp;", " ").replace("nbsp", " ");
		word = word.toLowerCase().trim();

		try {
			Float.valueOf(word);
			return false; // Numbers are not spelling errors 
		}
		catch (Exception e) {
		}

		//return word.length() >= 1; // Assume all words have spelling errors.
		return word.length() >= 2 && !dictionary.contains(word); // A word has a spelling error when it's not in the dictionary
	}
}
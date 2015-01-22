package edu.iastate.symex.util;

import java.util.HashMap;

/**
 * 
 * @author HUNG
 *
 */
public class StringUtils {
	
	/*
	 * Check string content
	 */
	
	public static boolean isWhitespace(String string) {
		for (int i = 0; i < string.length(); i++)
			if (!Character.isWhitespace(string.charAt(i)))
				return false;
		return true;
	}
	
	public static boolean isNumeric(String string) {
		try {
			Double.parseDouble(string);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	/*
	 * Escape/unescape strings
	 */
	
	public static String unescape(String stringValue, HashMap<Character, String> mapTable) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < stringValue.length(); i++) {
			if (stringValue.charAt(i) == '\\' && i < stringValue.length() - 1) {
				i++;
				Character character = stringValue.charAt(i);
				String unescapedString = mapTable.get(character);
				if (unescapedString != null)
					buffer.append(unescapedString);
				else {
					//logging.MyLogger.log(logging.MyLevel.USER_EXCEPTION, "In StringUtils.java: Unrecognized escape character \\" + character);
					buffer.append(" " + character); // Replace \ with a space
				}
			}
			else
				buffer.append(stringValue.charAt(i));
		}
		return buffer.toString();
	}
	
	public static String escape(String stringValue, HashMap<Character, String> mapTable) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < stringValue.length(); i++) {
			Character character = stringValue.charAt(i);
			String escapedString = mapTable.get(character);
			if (escapedString != null)
				buffer.append(escapedString);
			else
				buffer.append(character);
		}
		return buffer.toString();
	}
	
	public static String getUnescapedStringValuePreservingLength(String stringValue, char stringType) {
		if (stringType == '\"') {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', " \t");
			mapTable.put('r', " \r");
			mapTable.put('n', "\n ");		// Put the space after so that \r\n -> _[\r][\n]_
			mapTable.put('\\', " \\");
			mapTable.put('"', "\" ");		// Put the space after so that \\\" -> _\"_
			mapTable.put('\'', "\\\'");		// Keep \' as \' in quotes
			return StringUtils.unescape(stringValue, mapTable);
		}
		else if (stringType == '\'') {
			HashMap<Character, String> mapTable = new HashMap<Character, String>();
			mapTable.put('t', " \t");
			mapTable.put('r', " \r");
			mapTable.put('n', "\n ");		// Put the space after so that \r\n -> _[\r][\n]_
			mapTable.put('\\', " \\");
			mapTable.put('\'', "\' ");		// Put the space after so that \\\' -> _\'_
			mapTable.put('\"', "\\\"");		// Keep \" as \" in apostrophes
			return StringUtils.unescape(stringValue, mapTable);
		}
		else {
			// Do nothing
			return stringValue;
		}
	}
	
	/*
	 * Miscellaneous
	 */
	
	public static String getIndentedTabs(int numOfTabs) {
		String tabs = "";
		for (int i = 0; i < numOfTabs; i++)
			tabs = tabs + "\t";
		return tabs;
	}
	
}

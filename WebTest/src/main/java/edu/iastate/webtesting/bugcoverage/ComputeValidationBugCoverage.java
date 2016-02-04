package edu.iastate.webtesting.bugcoverage;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.filters.StringInputStream;
import org.w3c.tidy.Report;
import org.w3c.tidy.Tidy;
import org.w3c.tidy.Report.IErrorListener;

import edu.iastate.webtesting.evaluation.DebugInfo;
import edu.iastate.webtesting.evaluation.Utils;
import edu.iastate.webtesting.util_clone.CodeLocation;
import edu.iastate.webtesting.util_clone.Config;
import edu.iastate.webtesting.values_clone.CondValue;
import edu.iastate.webtesting.values_clone.Literal;

/**
 * 
 * @author HUNG
 *
 */
public class ComputeValidationBugCoverage {
	
	public Set<String> compute(String output, CondValue cModel) {
		List<String> clientBugs = getValidationBugs(output);
		List<String> serverBugs = clientToServerBugs(clientBugs, cModel);
		Set<String> bugs = filterServerBugs(serverBugs);
		
		// For debugging
		DebugInfo.validationBugCoverageComputed(clientBugs, serverBugs, bugs);
		
		return bugs;
	}
	
	private List<String> getValidationBugs(final String output) {
		final List<String> validationBugs = new ArrayList<String>();
	
		Tidy tidy = new Tidy();
		tidy.setErrout(new PrintWriter(new StringWriter()));
		
		Report.setErrorListener(new IErrorListener() {

			@Override
			public void onError(String error) {
				String errorMessage;
				if (error.contains(" - "))
					errorMessage = error.substring(error.indexOf(" - ") + " - ".length());
				else
					errorMessage = error;
				errorMessage = errorMessage.replace("\r", "").replace("\n", " ");
				
				String errorLocation;
				if (error.contains("line")) {
					errorLocation = error.substring(0, error.indexOf(" - "));
					
					int linePos = errorLocation.indexOf("line ");
					int columnPos = errorLocation.indexOf("column ");
					int line = Integer.valueOf(errorLocation.substring(linePos + "line ".length(), columnPos - 1).replace(",", ""));
					int column = Integer.valueOf(errorLocation.substring(columnPos + "column ".length()).replace(",", ""));
					
					// ADHOC Fix an incorrect position of error by Tidy
					try {
						findOffset(output, line, column);
					}
					catch (Exception e) {
						if (Config.SUBJECT_SYSTEM.equals("UPB-2.2.7")) {
							if (errorMessage.equals("Warning: img proprietary attribute value \"absmiddle\""))
								column = 1;
							else if (errorMessage.equals("Warning: missing </div>"))
								column = 1;
							else if (errorMessage.equals("Warning: <input> element not empty or not closed"))
								column = 1;
							else if (errorMessage.equals("Warning: inserting implicit <strong>"))
								column = 1;		
							else if (errorMessage.equals("Warning: Warning: unescaped & or unknown entity \"&page\""))
								column = 1;
							else if (errorMessage.equals("Warning: discarding unexpected </td>"))
								column = 1;
							else if (errorMessage.equals("Warning: discarding unexpected img"))
								column = 1;
							else if (errorMessage.equals("Warning: missing </div> before </fieldset>"))
								column = 1;
							else if (errorMessage.equals("Warning: inserting missing 'title' element"))
								column = 1;
							else if (errorMessage.equals("Warning: Warning: unescaped & or unknown entity \"&p_id\""))
								column = 1;
							else if (errorMessage.equals("Warning: br isn't allowed in <tbody> elements"))
								column = 1;
							else if (errorMessage.equals("Warning: missing </table>"))
								column = 1;
							else if (errorMessage.equals("Warning: trimming empty <tbody>"))
								column = 1;
							else if (errorMessage.equals("Warning: missing </form>"))
								column = 1;
							else if (errorMessage.equals("Warning: trimming empty <table>"))
								column = 1;
							else if (errorMessage.equals("Error: discarding unexpected </select>"))
								column = 1;
							else if (errorMessage.equals("Error: discarding unexpected br"))
								column = 1;
							else if (errorMessage.equals("Error: discarding unexpected <hr>"))
								column = 1;
							else if (errorMessage.equals("Error: discarding unexpected </blockquote>"))
								column = 1;
							else if (errorMessage.equals("Error: discarding unexpected </td>"))
								column = 1;
							else if (errorMessage.equals("Error: discarding unexpected img"))
								column = 1;
						}
						else if (Config.SUBJECT_SYSTEM.equals("WebChess-1.0.0")) {
							if (errorMessage.equals("Warning: trimming empty <p>"))
								column = 1;
							else if (errorMessage.equals("Warning: trimming empty <div>"))
								column = 1;
						}
						else if (Config.SUBJECT_SYSTEM.equals("WordPress-4.3.1")) {
							if (errorMessage.equals("Warning: missing </h3>"))
								column = 1;
							else if (errorMessage.equals("Warning: missing </ul>"))
								column = 1;
							else if (errorMessage.equals("Warning: missing </div>"))
								column = 1;
							else if (errorMessage.equals("Warning: missing </form>"))
								column = 1;
							else if (errorMessage.equals("Warning: trimming empty <div>"))
								column = 1;
							else if (errorMessage.equals("Warning: trimming empty <ul>"))
								column = 1;
							else if (errorMessage.equals("Warning: inserting missing 'title' element"))
								column = 1;
							else if (errorMessage.equals("Warning: unknown attribute \"required\""))
								column = 1;
							else if (errorMessage.equals("Warning: unknown attribute \"aria-required\""))
								column = 1;
							else if (errorMessage.equals("Warning: discarding unexpected </wfw:commentrss>"))
								column = 1;
							else if (errorMessage.equals("Warning: discarding unexpected </item>"))
								column = 1;
						}
					}
					// END OF ADHOC CODE
					
					int offset = findOffset(output, line, column);
					errorLocation = "offset " + offset + " line " + line + " column " + column;
				}
				else {
					errorLocation = "offset -1 line 0 column 0";
				}
				
				validationBugs.add(errorLocation + ": " + errorMessage);
			}
		});
		
		tidy.parse(new StringInputStream(output), new ByteArrayOutputStream());
		
		return validationBugs;
	}
	
	private static int findOffset(String output, int line, int column) {
		if (line <= 0 || column <= 0)
			return -1;
		
		int offset = 0;
		int line_ = 1;
		int column_ = 1;
		while (line_ < line || column_ < column) {
			// TODO Debug this: in some systems, output.length() > offset
			if (output.length() <= offset)
				return offset;
			if (output.charAt(offset) == '\n') {
				line_++;
				column_ = 1;
			}
			else
				column_++;
			offset++;
		}
		return offset;
	}
	
	public static List<String> clientToServerBugs(List<String> clientBugs, CondValue cModel) {
		List<String> severBugs = new ArrayList<String>();
		
		for (String error : clientBugs) {
			String errorMessage = error.substring(error.indexOf(':') + 2);
			String errorLocation = error.substring(0, error.indexOf(':'));
			int offsetPos = errorLocation.indexOf("offset ");
			int linePos = errorLocation.indexOf("line ");
			int offset = Integer.valueOf(errorLocation.substring(offsetPos + "offset ".length(), linePos - 1));
				
			String serverPosition = getServerCodePosition(offset, cModel);
			severBugs.add(serverPosition + ": " + errorMessage);
		}
			
		return severBugs;
	}
	
	private static String getServerCodePosition(int clientPosition, CondValue cModel) {
		if (clientPosition == -1)
			return "[Unresolved Location]";
		
		List<CondValue> condValues = Utils.flattenConcat(cModel);
		for (CondValue condValue : condValues) {
			if (condValue instanceof Literal) {
				Literal literal = (Literal) condValue;
				if (literal.getStringValue().length() <= clientPosition) {
					clientPosition -= ((Literal) condValue).getStringValue().length();
				}
				else {
					if (literal.getLocation() == CodeLocation.UNDEFINED)
						return "[Unresolved Location]";
					else
						return literal.getLocation().getFile() + "@" + (String.valueOf(literal.getLocation().getOffset() + clientPosition)); 	
				}
			}
			else {
				// TODO Handle other types of values
			}
		}
		return "[Unresolved Location]";
	}
	
	public static Set<String> filterServerBugs(List<String> serverBugs) {
		Set<String> bugs = new HashSet<String>();
		Set<String> bugLocations = new HashSet<String>();
		for (String bug : serverBugs) {
			String bugLocation = bug.substring(0, bug.indexOf(':'));
			if (bugLocation.equals("[Unresolved Location]")) // Ignore bugs that cannot be found on the server code
				continue;
			
			if (!bugLocations.contains(bugLocation)) {
				bugLocations.add(bugLocation);
				bugs.add(bug);
			}
		}
		return bugs;
	}
}
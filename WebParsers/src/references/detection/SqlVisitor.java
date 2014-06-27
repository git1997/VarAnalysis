package references.detection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import references.Reference;
import references.ReferenceManager;
import references.SqlTableColumnDecl;
import sourcetracing.Location;
import sourcetracing.SingleLocation;

/**
 * SqlVisitor visits some SQL code and detects entities.
 * 
 * @author HUNG
 *
 */
public class SqlVisitor {
	
	private String sqlCode;
	private Location sqlLocation;
	private String scope;
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public SqlVisitor(String sqlCode, Location sqlLocation, String scope, ReferenceManager referenceManager) {
		this.sqlCode = sqlCode;
		this.sqlLocation = sqlLocation;
		this.scope = scope;
		this.referenceManager = referenceManager;
	}
	
	/**
	 * Visits the SQL code.
	 */
	public void visit() {
		 Pattern p = Pattern.compile("SELECT (\\s*\\w+\\s*,)*(\\s*\\w+\\s*) FROM", Pattern.CASE_INSENSITIVE);
		 Matcher m = p.matcher(sqlCode);
		 if (m.lookingAt()) {
			 String sqlTableColumns = m.group().substring("SELECT ".length(), m.group().length() - " FROM".length());
			 
			 String[] sqlTableColumnParts = sqlTableColumns.split("\\s*,\\s*");
			 for (String sqlTableColumn : sqlTableColumnParts) {
				 int offset = sqlCode.indexOf(sqlTableColumn);
				 Location location = new SingleLocation(sqlLocation, offset);
				 
				 Reference reference = new SqlTableColumnDecl(sqlTableColumn, location, scope);
				 referenceManager.addReference(reference);
			 }
		 }
	}

}

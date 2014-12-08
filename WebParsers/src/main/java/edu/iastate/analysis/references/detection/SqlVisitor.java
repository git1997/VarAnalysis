package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.analysis.references.SqlTableColumnDecl;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.RelativeRange;

/**
 * SqlVisitor visits some SQL code and detects entities.
 * 
 * @author HUNG
 *
 */
public class SqlVisitor {
	
	private String sqlCode;
	private PositionRange sqlLocation;
	private String sqlScope;
	
	private File entryFile;
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public SqlVisitor(String sqlCode, PositionRange sqlLocation, String sqlScope, File entryFile, ReferenceManager referenceManager) {
		this.sqlCode = sqlCode;
		this.sqlLocation = sqlLocation;
		this.sqlScope = sqlScope;
		
		this.entryFile = entryFile;
		this.referenceManager = referenceManager;
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference) {
		// TODO Set constraints?
		reference.setEntryFile(entryFile);
		referenceManager.addReference(reference);
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
				 PositionRange location = new RelativeRange(sqlLocation, offset, sqlTableColumn.length());
				 
				 Reference reference = new SqlTableColumnDecl(sqlTableColumn, location, sqlScope);
				 addReference(reference);
			 }
		 }
	}

}

package references;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class PhpRefToSqlTableColumn extends RegularReference {

	private String scope;	// The scope of this PhpRefToSqlTableColumn (e.g. 'mysql_query_123456')
							// @see php.nodes.FunctionInvocationNode.php_mysql_query(ArrayList<DataNode>, ElementManager, Object)).
	
	/**
	 * Constructor
	 */
	public PhpRefToSqlTableColumn(String name, Location location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the scope of this PhpRefToSqlTableColumn.
	 */
	public String getScope() {
		return scope;
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof SqlTableColumnDecl) {
			SqlTableColumnDecl sqlTableColumnDecl = (SqlTableColumnDecl) declaringReference;
			return getName().equals(sqlTableColumnDecl.getName())
					&& getScope().equals(sqlTableColumnDecl.getScope());
		}
		else
			return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see references.Reference#writePropertiesToXmlElement(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	@Override
	public void writePropertiesToXmlElement(Document document, Element referenceElement) {
		super.writePropertiesToXmlElement(document, referenceElement);
		if (scope != null)
			referenceElement.setAttribute("Scope", scope);
	}
	
	/*
	 * (non-Javadoc)
	 * @see references.Reference#readPropertiesFromXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public void readPropertiesFromXmlElement(Element referenceElement) {
		super.readPropertiesFromXmlElement(referenceElement);
		if (referenceElement.hasAttribute("Scope"))
			scope = referenceElement.getAttribute("Scope");
	}
	
}

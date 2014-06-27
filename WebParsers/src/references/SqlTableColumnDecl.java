package references;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class SqlTableColumnDecl extends DeclaringReference {

	private String scope;	// The scope of this SqlTableColumnDecl (e.g. 'mysql_query_123456')
							// @see php.nodes.FunctionInvocationNode.php_mysql_query(ArrayList<DataNode>, ElementManager, Object)).
	
	/**
	 * Constructor
	 */
	public SqlTableColumnDecl(String name, Location location, String scope) {
		super(name, location);
		this.scope = scope;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the scope of this SqlTableColumnDecl.
	 */
	public String getScope() {
		return scope;
	}
	
	/*
	 * (non-Javadoc)
	 * @see references.Reference#sameAs(references.Reference)
	 */
	@Override
	public boolean sameAs(Reference reference) {
		return super.sameAs(reference)
				&& (getScope().equals(((SqlTableColumnDecl) reference).getScope()));
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

package references;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class JsRefToHtmlInput extends RegularReference {

	private String formName;
	
	/**
	 * Constructor
	 */
	public JsRefToHtmlInput(String name, Location location, String formName) {
		super(name, location);
		this.formName = formName;
	}
	
	/*
	 * Get properties
	 */
	
	public String getFormName() {
		return formName;
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof HtmlInputDecl) {
			HtmlInputDecl htmlInputDecl = (HtmlInputDecl) declaringReference;
			return getName().equals(htmlInputDecl.getName())
					&& (htmlInputDecl.getFormName() == null || getFormName().equals(htmlInputDecl.getFormName()));
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
		if (formName != null)
			referenceElement.setAttribute("FormName", formName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see references.Reference#readPropertiesFromXmlElement(org.w3c.dom.Element)
	 */
	@Override
	public void readPropertiesFromXmlElement(Element referenceElement) {
		super.readPropertiesFromXmlElement(referenceElement);
		if (referenceElement.hasAttribute("FormName"))
			formName = referenceElement.getAttribute("FormName");
	}

}

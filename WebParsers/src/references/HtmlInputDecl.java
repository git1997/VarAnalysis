package references;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class HtmlInputDecl extends DeclaringReference {

	private String formName;		// Can be null
	private String submitToPage;	// Can be null
	
	/**
	 * Constructor
	 */
	public HtmlInputDecl(String name, Location location, String formName, String submitToPage) {
		super(name, location);
		this.formName = formName;
		this.submitToPage = submitToPage;
	}
	
	/*
	 * Get properties
	 */
	
	/**
	 * Returns the name of this input's form, can be null.
	 */
	public String getFormName() {
		return formName;
	}
	
	/**
	 * Returns the submitted page of this input's form, can be null.
	 */
	public String getSubmitToPage() {
		return submitToPage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see references.Reference#sameAs(references.Reference)
	 */
	@Override
	public boolean sameAs(Reference reference) {
		return super.sameAs(reference)
				&& (getFormName() == null || ((HtmlInputDecl) reference).getFormName() == null || getFormName().equals(((HtmlInputDecl) reference).getFormName()));
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
		if (submitToPage != null)
			referenceElement.setAttribute("SubmitToPage", submitToPage);
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
		if (referenceElement.hasAttribute("SubmitToPage"))
			submitToPage = referenceElement.getAttribute("SubmitToPage");
	}

}

package references.detection;

import constraints.Constraint;
import deprecated.html.elements.HtmlAttribute;
import deprecated.html.elements.HtmlAttributeValue;
import deprecated.html.elements.HtmlElement;
import deprecated.html.elements.HtmlFormTag;
import deprecated.html.elements.HtmlInputTag;
import deprecated.html.elements.HtmlScriptTag;
import deprecated.html.elements.HtmlText;
import references.HtmlFormDecl;
import references.HtmlIdDecl;
import references.HtmlInputDecl;
import references.HtmlQueryDecl;
import references.Reference;
import references.ReferenceManager;
import sourcetracing.Location;
import sourcetracing.SingleLocation;
import sourcetracing.SourceCodeLocation;
import util.FileIO;
import logging.MyLevel;
import logging.MyLogger;

/**
 * HtmlVisitor visits HTML elements and detects entities.
 * 
 * @author HUNG
 *
 */
public class HtmlVisitor {
	
	private String projectFolder;
	private Constraint constraint;
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public HtmlVisitor(String projectFolder, Constraint constraint, ReferenceManager referenceManager) {
		this.projectFolder = projectFolder;
		this.constraint = constraint;
		this.referenceManager = referenceManager;
	}
	
	/**
	 * Visits a general HtmlElement
	 * @param htmlElement
	 */
	public void visit(HtmlElement htmlElement) {
		if (htmlElement instanceof HtmlScriptTag)
			visit((HtmlScriptTag) htmlElement);
		
		else if (htmlElement instanceof HtmlAttribute)
			visit((HtmlAttribute) htmlElement);
		
		else {
			// Do nothing
		}
	}
	
	/**
	 * Visits an HtmlScriptTag
	 * @param scriptTag
	 */
	public void visit(HtmlScriptTag scriptTag) {
		/*
		 * Handle <script type="text/javascript" src="javascript.js"/>
		 */
		if (scriptTag.getAttribute("src") != null) {
			String includedFile = scriptTag.getAttribute("src").getStringValue();
			String currentFilePath = scriptTag.getLocation().getLocationAtOffset(0).getFilePath();
			String includedFilePath = FileIO.resolveIncludedFilePath(projectFolder, currentFilePath, includedFile);
			
			if (includedFilePath == null) {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlVisitor.java: Unable to resolve the included file " + includedFile + ". Current file: " + currentFilePath);
			}
			else {
				String javascriptSource = FileIO.readStringFromFile(projectFolder + "\\" + includedFilePath);
				Location javascriptLocation = new SourceCodeLocation(includedFilePath, 0);
			
				ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, referenceManager);
			}
			return;
		}
		
		/*
		 * Handle <script type="text/javascript">Javascript code</script>
		 */
			
		if (scriptTag.getChildElements().size() != 1 || !(scriptTag.getChildElements().get(0) instanceof HtmlText)) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlVisitor.java: HtmlScriptTag does not have 1 child node of type HtmlText.");
			return;
		}
		
		// Get the Javascript source code and location
		HtmlText htmlText = (HtmlText) scriptTag.getChildElements().get(0);
		String javascriptSource = htmlText.getStringValue();
		Location javascriptLocation = htmlText.getLocation();
		
		// Replace the string "<!--" and "-->" so that the Javascript source code can be parsed.
		// Also make sure that the length is unchanged so that the entities can be correctly traced later.
		javascriptSource = javascriptSource.replaceAll("<!--", "    ").replaceAll("-->", "   ");
		
		ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, referenceManager);
	}
	
	/**
	 * Visits an HtmlAttribute
	 * @param attribute
	 */
	public void visit(HtmlAttribute attribute) {
		if (attribute.getValue() == null || attribute.getValue().getStringValue().isEmpty() || attribute.getValue().getLocation().getLocationAtOffset(0).isUndefined())
			return;
		
		else if (attribute.isNameAttribute())
			createEntitiesFromNameAttribute(attribute);
		
		else if (attribute.isIdAttribute())
			createEntitiesFromIdAttribute(attribute);
		
		else if (attribute.containsJavascript())
			findEntitiesInEventHandler(attribute.getValue());
		
		else if (attribute.containsQueryString())
			findEntitiesInQueryString(attribute.getValue());
	}

	/**
	 * Creates entities from an HtmlAttribute "name", e.g. <input name="my_input">
	 */
	private void createEntitiesFromNameAttribute(HtmlAttribute attribute) {
		Reference reference = null;	
		
		//----- Create HtmlForm entity, e.g. <form name="my_form">
		if (attribute.getParentTag() instanceof HtmlFormTag) {
			String formName = attribute.getValue().getStringValue();
			Location formLocation = attribute.getValue().getLocation();
			
			reference = new HtmlFormDecl(formName, formLocation);
		}			
		
		//----- Create HtmlInput entity, e.g. <input name="my_input">
		else if (attribute.getParentTag() instanceof HtmlInputTag) {
			HtmlInputTag inputTag = (HtmlInputTag) attribute.getParentTag();
			HtmlFormTag formTag = inputTag.getParentFormTag();
			
			String inputName = attribute.getValue().getStringValue();
			Location inputLocation = attribute.getValue().getLocation();
			
			if (inputName.contains("[")) { // e.g. <input type='checkbox' name='addr[email]' />
				inputName = inputName.substring(0, inputName.indexOf('['));
			}
			
			if (formTag != null)
				reference = new HtmlInputDecl(inputName, inputLocation, formTag.getFormName(), formTag.getFormSubmitToPage());
			else
				reference = new HtmlInputDecl(inputName, inputLocation, "", "");
		}	
		
		//----- Create regular HtmlTag entity, e.g. <a name="my_link">
		else {
			// Do nothing
		}
		
		// Add the reference
		if (reference != null) {
			reference.setConstraint(constraint);
			referenceManager.addReference(reference);
		}
	}
	
	/**
	 * Creates entities from an HtmlAttribute "id", e.g. <div id="mydiv">
	 */
	private void createEntitiesFromIdAttribute(HtmlAttribute attribute) {
		String id = attribute.getValue().getStringValue();
		Location location = attribute.getValue().getLocation();
		
		Reference reference = new HtmlIdDecl(id, location);
		reference.setConstraint(constraint);
		referenceManager.addReference(reference);
	}
	
	/**
	 * Finds entities in an HTML event handler, e.g. <body onload="sayHello();">
	 */
	private void findEntitiesInEventHandler(HtmlAttributeValue attributeValue) {
		String javascriptSource = attributeValue.getStringValue();
		Location javascriptLocation = attributeValue.getLocation();
		ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, referenceManager);
	}
	
	/**
	 * Finds entities in an HTML query string, e.g. "<a href = "google.com?my_input1=value1&my_input2=value2/>"
	 */
	private void findEntitiesInQueryString(HtmlAttributeValue attributeValue) {
		String queryString = attributeValue.getStringValue();
		if (!queryString.contains("?"))
			return;
		
		//String queryStringUrl = queryString.substring(0, queryString.indexOf("?"));
		String queryStringPairs = queryString.substring(queryString.indexOf("?") + 1);
		
		String[] nameValuePairParts = queryStringPairs.split("&amp;");
		for (String nameValuePairPart : nameValuePairParts) {
			String[] nameValuePairs = nameValuePairPart.split("&");
			
			for (String nameValuePair : nameValuePairs) {
				String nameValueParts[] = nameValuePair.split("=");
				String name = (nameValueParts.length > 0 ? nameValueParts[0] : "");
				//String value = (nameValueParts.length == 2 ? nameValueParts[1] : "");
				int offset = queryString.indexOf(nameValuePair);
				if (name.isEmpty())
					continue;
				
				// Add the reference
				Reference reference = new HtmlQueryDecl(name, new SingleLocation(attributeValue.getLocation(), offset));
				reference.setConstraint(constraint);
				referenceManager.addReference(reference);
			}
		}
	}
	
}

package edu.iastate.analysis.references.detection;

import java.io.File;

import edu.iastate.analysis.references.HtmlFormDecl;
import edu.iastate.analysis.references.HtmlIdDecl;
import edu.iastate.analysis.references.HtmlInputDecl;
import edu.iastate.analysis.references.HtmlQueryDecl;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlConcat;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlForm;
import edu.iastate.parsers.html.dom.nodes.HtmlInput;
import edu.iastate.parsers.html.dom.nodes.HtmlNode;
import edu.iastate.parsers.html.dom.nodes.HtmlScript;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.dom.nodes.HtmlText;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.constraints.ConstraintFactory;
import edu.iastate.symex.position.PositionRange;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.position.RelativeRange;
import edu.iastate.symex.util.FileIO;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;

/**
 * HtmlVisitor visits HTML elements and detects entities.
 * 
 * @author HUNG
 *
 */
public class HtmlVisitor {
	
	private ReferenceManager referenceManager;
	
	/**
	 * Constructor
	 */
	public HtmlVisitor(ReferenceManager referenceManager) {
		this.referenceManager = referenceManager;
	}
	
	/**
	 * 
	 * @param htmlDocument
	 */
	public void visitDocument(HtmlDocument htmlDocument) {
		Constraint constraint = Constraint.TRUE;
		for (HtmlNode htmlNode : htmlDocument.getChildNodes())
			visitNode(htmlNode, constraint);
	}
	
	/**
	 * Visits a general HtmlNode
	 * @param htmlNode
	 */
	public void visitNode(HtmlNode htmlNode, Constraint constraint) {
		if (htmlNode instanceof HtmlConcat)
			visitConcat((HtmlConcat) htmlNode, constraint);
		
		else if (htmlNode instanceof HtmlSelect)
			visitSelect((HtmlSelect) htmlNode, constraint);
		
		else if (htmlNode instanceof HtmlElement)
			visitElement((HtmlElement) htmlNode, constraint);
		
		else if (htmlNode instanceof HtmlAttribute)
			visitAttribute((HtmlAttribute) htmlNode, constraint);
		
		else {
			// Do nothing
		}
	}
	
	/**
	 * Visits an HtmlConcat
	 * @param htmlConcat
	 */
	public void visitConcat(HtmlConcat htmlConcat, Constraint constraint) {
		for (HtmlNode htmlNode : htmlConcat.getChildNodes())
			visitNode(htmlNode, constraint);
	}
	
	/**
	 * Visits an HtmlSelect
	 * @param htmlSelect
	 */
	public void visitSelect(HtmlSelect htmlSelect, Constraint constraint) {
		Constraint trueConstraint = ConstraintFactory.createAndConstraint(constraint, htmlSelect.getConstraint());
		Constraint falseConstraint = ConstraintFactory.createAndConstraint(constraint, ConstraintFactory.createNotConstraint(htmlSelect.getConstraint()));
		visitNode(htmlSelect.getTrueBranchNode(), trueConstraint);
		visitNode(htmlSelect.getFalseBranchNode(), falseConstraint);
	}
	
	/**
	 * Visits a general HtmlElement
	 * @param htmlElement
	 */
	public void visitElement(HtmlElement htmlElement, Constraint constraint) {
		if (htmlElement instanceof HtmlScript)
			visitScript((HtmlScript) htmlElement, constraint);
		else {
			for (HtmlAttribute attr : htmlElement.getAttributes())
				visitAttribute(attr, constraint);
			for (HtmlNode child : htmlElement.getChildNodes())
				visitNode(child, constraint);
		}
	}
	
	/**
	 * Visits an HtmlScript
	 * @param htmlScript
	 */
	public void visitScript(HtmlScript htmlScript, Constraint constraint) {
		/*
		 * Handle <script type="text/javascript" src="javascript.js"/>
		 */
		if (htmlScript.getAttributeValue("src") != null) {
			// TODO Fix this
			String includedFile = htmlScript.getAttributeValue("src").getStringValue();
			File currentFile = htmlScript.getLocation().getStartPosition().getFile();
			String includedFilePath = FileIO.resolveIncludedFilePath(currentFile.getParent(), currentFile.getAbsolutePath(), includedFile);
			
			if (includedFilePath == null) {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlVisitor.java: Unable to resolve the included file " + includedFile + ". Current file: " + currentFile.getAbsolutePath());
			}
			else {
				File included = new File(currentFile.getParent() + "\\" + includedFilePath);
				String javascriptSource = FileIO.readStringFromFile(included);
				Range javascriptLocation = new Range(included, 0, javascriptSource.length());
			
				ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, referenceManager);
			}
			return;
		}
		
		/*
		 * Handle <script type="text/javascript">Javascript code</script>
		 */
			
		if (htmlScript.getChildNodes().size() != 1 || !(htmlScript.getChildNodes().get(0) instanceof HtmlText)) {
			MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlVisitor.java: HtmlScriptTag does not have 1 child node of type HtmlText.");
			return;
		}
		
		// Get the Javascript source code and location
		HtmlText htmlText = (HtmlText) htmlScript.getChildNodes().get(0);
		String javascriptSource = htmlText.getStringValue();
		PositionRange javascriptLocation = htmlText.getLocation();
		
		// Replace the string "<!--" and "-->" so that the Javascript source code can be parsed.
		// Also make sure that the length is unchanged so that the entities can be correctly traced later.
		javascriptSource = javascriptSource.replaceAll("<!--", "    ").replaceAll("-->", "   ");
		
		ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, referenceManager);
	}
	
	/**
	 * Visits an HtmlAttribute
	 * @param attribute
	 */
	public void visitAttribute(HtmlAttribute attribute, Constraint constraint) {
		if (attribute.getValue() == null || attribute.getValue().isEmpty() || attribute.getAttributeValue().getLocation().isUndefined())
			return;
		
		else if (attribute.isNameAttribute())
			createEntitiesFromNameAttribute(attribute, constraint);
		
		else if (attribute.isIdAttribute())
			createEntitiesFromIdAttribute(attribute, constraint);
		
		else if (attribute.containsJavascript())
			findEntitiesInEventHandler(attribute.getAttributeValue(), constraint);
		
		else if (attribute.containsQueryString())
			findEntitiesInQueryString(attribute.getAttributeValue(), constraint);
	}

	/**
	 * Creates entities from an HtmlAttribute "name", e.g. <input name="my_input">
	 */
	private void createEntitiesFromNameAttribute(HtmlAttribute attribute, Constraint constraint) {
		Reference reference = null;	
		
		//----- Create HtmlForm entity, e.g. <form name="my_form">
		if (attribute.getParentElement() instanceof HtmlForm) {
			String formName = attribute.getValue();
			PositionRange formLocation = attribute.getAttributeValue().getLocation();
			
			reference = new HtmlFormDecl(formName, formLocation);
		}			
		
		//----- Create HtmlInput entity, e.g. <input name="my_input">
		else if (attribute.getParentElement() instanceof HtmlInput) {
			HtmlInput inputTag = (HtmlInput) attribute.getParentElement();
			HtmlForm formTag = inputTag.getParentForm();
			
			String inputName = attribute.getValue();
			PositionRange inputLocation = attribute.getAttributeValue().getLocation();
			
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
	private void createEntitiesFromIdAttribute(HtmlAttribute attribute, Constraint constraint) {
		String id = attribute.getValue();
		PositionRange location = attribute.getAttributeValue().getLocation();
		
		Reference reference = new HtmlIdDecl(id, location);
		reference.setConstraint(constraint);
		referenceManager.addReference(reference);
	}
	
	/**
	 * Finds entities in an HTML event handler, e.g. <body onload="sayHello();">
	 */
	private void findEntitiesInEventHandler(HtmlAttributeValue attributeValue, Constraint constraint) {
		String javascriptSource = attributeValue.getStringValue();
		PositionRange javascriptLocation = attributeValue.getLocation();
		ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, referenceManager);
	}
	
	/**
	 * Finds entities in an HTML query string, e.g. "<a href = "google.com?my_input1=value1&my_input2=value2/>"
	 */
	private void findEntitiesInQueryString(HtmlAttributeValue attributeValue, Constraint constraint) {
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
				Reference reference = new HtmlQueryDecl(name, new RelativeRange(attributeValue.getLocation(), offset, name.length()));
				reference.setConstraint(constraint);
				referenceManager.addReference(reference);
			}
		}
	}
	
}

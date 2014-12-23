package edu.iastate.analysis.references.detection;

import java.io.File;
import java.util.HashMap;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.HtmlDeclOfHtmlInputValue;
import edu.iastate.analysis.references.HtmlFormDecl;
import edu.iastate.analysis.references.HtmlIdDecl;
import edu.iastate.analysis.references.HtmlInputDecl;
import edu.iastate.analysis.references.HtmlQueryDecl;
import edu.iastate.analysis.references.Reference;
import edu.iastate.parsers.html.dom.nodes.HtmlAttribute;
import edu.iastate.parsers.html.dom.nodes.HtmlAttributeValue;
import edu.iastate.parsers.html.dom.nodes.HtmlElement;
import edu.iastate.parsers.html.dom.nodes.HtmlForm;
import edu.iastate.parsers.html.dom.nodes.HtmlInput;
import edu.iastate.parsers.html.dom.nodes.HtmlNodeVisitor;
import edu.iastate.parsers.html.dom.nodes.HtmlScript;
import edu.iastate.parsers.html.dom.nodes.HtmlSelect;
import edu.iastate.parsers.html.sax.nodes.HText;
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
public class HtmlVisitor extends HtmlNodeVisitor {
	
	private File entryFile;
	private Constraint constraint; // Could be updated during traversal
			
	private ReferenceManager referenceManager;
	
	private HashMap<HtmlElement, DeclaringReference> declMap = new HashMap<HtmlElement, DeclaringReference>();
	
	/**
	 * Constructor
	 */
	public HtmlVisitor(File entryFile, ReferenceManager referenceManager) {
		this.entryFile = entryFile;
		this.constraint = Constraint.TRUE;
		this.referenceManager = referenceManager;
	}
	
	/**
	 * Adds a reference.
	 * This method should be called instead of calling referenceManager.addReference directly.
	 */
	private void addReference(Reference reference) {
		reference.setEntryFile(entryFile);
		reference.setConstraint(constraint);
		referenceManager.addReference(reference);
	}
	
	/**
	 * Visits an HtmlSelect
	 */
	@Override
	public void visitSelect(HtmlSelect htmlSelect) {
		Constraint savedConstraint = constraint;

		constraint = ConstraintFactory.createAndConstraint(savedConstraint, htmlSelect.getConstraint());
		visit(htmlSelect.getTrueBranchNode());

		constraint = ConstraintFactory.createAndConstraint(savedConstraint, ConstraintFactory.createNotConstraint(htmlSelect.getConstraint()));
		visit(htmlSelect.getFalseBranchNode());

		constraint = savedConstraint;
	}
	
	/**
	 * Visits an HtmlElement
	 */
	@Override
	public void visitElement(HtmlElement htmlElement) {
		if (htmlElement instanceof HtmlScript)
			visitScript((HtmlScript) htmlElement);
		else 
			super.visitElement(htmlElement);
	}
	
	/**
	 * Visits an HtmlScript
	 */
	private void visitScript(HtmlScript htmlScript) {
		/*
		 * Handle <script type="text/javascript" src="javascript.js"/>
		 */
		if (htmlScript.getAttributeValue("src") != null) {
			// TODO Review this code
			String includedFile = htmlScript.getAttributeValue("src").getStringValue();
			File currentFile = htmlScript.getLocation().getStartPosition().getFile();
			String includedFilePath = FileIO.resolveIncludedFilePath(currentFile.getParent(), currentFile.getAbsolutePath(), includedFile);
			
			if (includedFilePath == null) {
				MyLogger.log(MyLevel.USER_EXCEPTION, "In HtmlVisitor.java: Unable to resolve the included file " + includedFile + ". Current file: " + currentFile.getAbsolutePath());
			}
			else {
				File included = new File(currentFile.getParent() + File.separatorChar + includedFilePath);
				String javascriptSource = FileIO.readStringFromFile(included);
				Range javascriptLocation = new Range(included, 0, javascriptSource.length());
			
				ReferenceDetector.findReferencesInJavascriptCode(javascriptSource, javascriptLocation, constraint, entryFile, referenceManager);
			}
			return;
		}
		
		/*
		 * Handle <script type="text/javascript">Javascript code</script>
		 */

		// Get the JavaScript source code and location
		HText text = htmlScript.getSourceCode();
		String javascriptCode = text.getStringValue();
		PositionRange javascriptLocation = text.getLocation();
		
		// Replace the string "<!--" and "-->" so that the JavaScript source code can be parsed.
		// Also make sure that the length is unchanged so that the entities can be correctly traced later.
		javascriptCode = javascriptCode.replaceAll("<!--", "    ").replaceAll("-->", "   ");
		
		ReferenceDetector.findReferencesInJavascriptCode(javascriptCode, javascriptLocation, constraint, entryFile, referenceManager);
	}
	
	/**
	 * Visits an HtmlAttribute
	 */
	@Override
	public void visitAttribute(HtmlAttribute attribute) {
		if (attribute.getStringValue().isEmpty() || attribute.getAttributeValue().getLocation().isUndefined())
			return;
		
		if (attribute.isNameAttribute())
			createEntitiesFromNameAttribute(attribute);
		
		else if (attribute.isIdAttribute())
			createEntitiesFromIdAttribute(attribute);
		
		else if (attribute.isValueAttribute())
			createEntitiesFromValueAttribute(attribute);
		
		else if (attribute.containsJavascript())
			findEntitiesInEventHandler(attribute.getAttributeValue());
		
		else if (attribute.containsQueryString())
			findEntitiesInQueryString(attribute.getAttributeValue());
	}

	/**
	 * Creates entities from an HtmlAttribute "name", e.g. <input name="my_input">
	 */
	private void createEntitiesFromNameAttribute(HtmlAttribute attribute) {
		Reference reference = null;	
		
		//----- Create HtmlForm entity, e.g. <form name="my_form">
		if (attribute.getParentElement() instanceof HtmlForm) {
			String formName = attribute.getStringValue();
			PositionRange formLocation = attribute.getAttributeValue().getLocation();
			String submitToPage = ((HtmlForm) attribute.getParentElement()).getFormSubmitToPage();
			
			reference = new HtmlFormDecl(formName, formLocation, submitToPage);
			
			declMap.put((HtmlForm) attribute.getParentElement(), (HtmlFormDecl) reference);
		}			
		
		//----- Create HtmlInput entity, e.g. <input name="my_input">
		else if (attribute.getParentElement() instanceof HtmlInput) {
			HtmlInput inputTag = (HtmlInput) attribute.getParentElement();
			HtmlForm formTag = inputTag.getParentFormOrNull();
			
			String inputName = attribute.getStringValue();
			PositionRange inputLocation = attribute.getAttributeValue().getLocation();
			
			if (inputName.contains("[")) { // e.g. <input type='checkbox' name='addr[email]' />
				inputName = inputName.substring(0, inputName.indexOf('['));
			}
			
			if (formTag != null)
				reference = new HtmlInputDecl(inputName, inputLocation, (HtmlFormDecl) declMap.get(formTag));
			else
				reference = new HtmlInputDecl(inputName, inputLocation, null);
			
			declMap.put((HtmlInput) attribute.getParentElement(), (HtmlInputDecl) reference);
		}	
		
		//----- Create regular HtmlTag entity, e.g. <a name="my_link">
		else {
			// Do nothing
		}
		
		// Add the reference
		if (reference != null)
			addReference(reference);
	}
	
	/**
	 * Creates entities from an HtmlAttribute "id", e.g. <div id="mydiv">
	 */
	private void createEntitiesFromIdAttribute(HtmlAttribute attribute) {
		String id = attribute.getStringValue();
		PositionRange location = attribute.getAttributeValue().getLocation();
		
		Reference reference = new HtmlIdDecl(id, location);
		addReference(reference);
	}
	
	/**
	 * Creates entities from an HtmlAttribute "value", e.g. <input name="my_input" value="0">
	 */
	private void createEntitiesFromValueAttribute(HtmlAttribute attribute) {
		// Create an HtmlDeclOfHtmlInputValue
		if (attribute.getParentElement() instanceof HtmlInput) {
			HtmlInput inputTag = (HtmlInput) attribute.getParentElement();
		
			Reference reference = new HtmlDeclOfHtmlInputValue(attribute.getName(), attribute.getLocation(), (HtmlInputDecl) declMap.get(inputTag));
			addReference(reference);
		}	
	}
	
	/**
	 * Finds entities in an HTML event handler, e.g. <body onload="sayHello();">
	 */
	private void findEntitiesInEventHandler(HtmlAttributeValue attributeValue) {
		String javascriptCode = attributeValue.getStringValue();
		PositionRange javascriptLocation = attributeValue.getLocation();
		ReferenceDetector.findReferencesInJavascriptCode(javascriptCode, javascriptLocation, constraint, entryFile, referenceManager);
	}
	
	/**
	 * Finds entities in an HTML query string, e.g. "<a href = "google.com?my_input1=value1&my_input2=value2/>"
	 */
	private void findEntitiesInQueryString(HtmlAttributeValue attributeValue) {
		String queryString = attributeValue.getStringValue();
		if (!queryString.contains("?"))
			return;
		
		String queryStringUrl = queryString.substring(0, queryString.indexOf("?"));
		String queryStringPairs = queryString.substring(queryString.indexOf("?") + 1);
		
		String[] nameValuePairParts = queryStringPairs.split("&amp;");
		for (String nameValuePairPart : nameValuePairParts) {
			String[] nameValuePairs = nameValuePairPart.split("&");
			
			for (String nameValuePair : nameValuePairs) {
				String nameValueParts[] = nameValuePair.split("=");
				String name = (nameValueParts.length > 0 ? nameValueParts[0] : "");
				//String value = (nameValueParts.length == 2 ? nameValueParts[1] : "");
				if (name.isEmpty())
					continue;
				
				int offset = queryString.indexOf(nameValuePair);
				PositionRange location = new RelativeRange(attributeValue.getLocation(), offset, name.length());
				
				// Add the reference
				Reference reference = new HtmlQueryDecl(name, location, queryStringUrl);
				addReference(reference);
			}
		}
	}
	
}

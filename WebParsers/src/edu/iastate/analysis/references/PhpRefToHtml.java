package edu.iastate.analysis.references;

import edu.iastate.symex.position.PositionRange;

/**
 * 
 * @author HUNG
 *
 */
public class PhpRefToHtml extends RegularReference {

	/**
	 * Constructor
	 */
	public PhpRefToHtml(String name, PositionRange location) {
		super(name, location);
	}

	@Override
	public boolean refersTo(DeclaringReference declaringReference) {
		if (declaringReference instanceof HtmlInputDecl) {
			HtmlInputDecl htmlInputDecl = (HtmlInputDecl) declaringReference;
			return getName().equals(htmlInputDecl.getName());
		}
		else if (declaringReference instanceof HtmlQueryDecl) {
			HtmlQueryDecl htmlQueryDecl = (HtmlQueryDecl) declaringReference;
			return getName().equals(htmlQueryDecl.getName());
		}
		else
			return false;
		
		/*
		 * TODO Compare pages
		 */
		
//		if (connectHtmlInputWithPhpRequestVariables) {
//			if (entity.getSubmitToPage() != null) {	// HtmlInput entity
//				String key = entity.getSubmitToPage() + "@" + entity.getName();
//				
//				Entity oldEntity = mapFileToPhpRequestVariable.get(key);
//				if (oldEntity != null && oldEntity != entity) {
//					mergeEntities(oldEntity, entity);
//				}
//				oldEntity = mapFileToSubmittedHtmlInput.get(key);
//				if (oldEntity != null && oldEntity != entity) {
//					mergeEntities(oldEntity, entity);
//				}
//				
//				mapFileToSubmittedHtmlInput.put(key, entity);
//				entity.setSubmitToPage(null);
//			}
//			else if (entity.getOnPage() != null) { // PhpRequestVariable entity
//				String key = entity.getOnPage() + "@" + entity.getName();
//				
//				Entity oldEntity = mapFileToPhpRequestVariable.get(key);
//				if (oldEntity != null && oldEntity != entity) {
//					mergeEntities(oldEntity, entity);
//				}
//				oldEntity = mapFileToSubmittedHtmlInput.get(key);
//				if (oldEntity != null && oldEntity != entity) {
//					mergeEntities(oldEntity, entity);
//				}
//				
//				mapFileToPhpRequestVariable.put(key, entity);
//				entity.setOnPage(null);
//			}
//		}
		
	}

}

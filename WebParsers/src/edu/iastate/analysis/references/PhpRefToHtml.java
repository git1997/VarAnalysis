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
	public boolean sameEntityAs(DeclaringReference declaringReference) {
		return (declaringReference instanceof HtmlInputDecl || declaringReference instanceof HtmlQueryDecl)
				&& hasSameName(declaringReference);
		
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

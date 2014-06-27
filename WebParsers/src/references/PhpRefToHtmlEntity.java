package references;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public class PhpRefToHtmlEntity extends RegularReference {

	//private String onPage;
	
	/**
	 * Constructor
	 */
	public PhpRefToHtmlEntity(String name, Location location, String onPage) {
		super(name, location);
		//this.onPage = onPage;
	}

	/*
	 * (non-Javadoc)
	 * @see references.RegularReference#refersTo(references.DeclaringReference)
	 */
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
		// TODO: Compare page
//		if (connectHtmlInputWithPhpRequestVariables) {
//		if (entity.getSubmitToPage() != null) {	// HtmlInput entity
//			String key = entity.getSubmitToPage() + "@" + entity.getName();
//			
//			Entity oldEntity = mapFileToPhpRequestVariable.get(key);
//			if (oldEntity != null && oldEntity != entity) {
//				mergeEntities(oldEntity, entity);
//			}
//			oldEntity = mapFileToSubmittedHtmlInput.get(key);
//			if (oldEntity != null && oldEntity != entity) {
//				mergeEntities(oldEntity, entity);
//			}
//			
//			mapFileToSubmittedHtmlInput.put(key, entity);
//			entity.setSubmitToPage(null);
//		}
//		else if (entity.getOnPage() != null) { // PhpRequestVariable entity
//			String key = entity.getOnPage() + "@" + entity.getName();
//			
//			Entity oldEntity = mapFileToPhpRequestVariable.get(key);
//			if (oldEntity != null && oldEntity != entity) {
//				mergeEntities(oldEntity, entity);
//			}
//			oldEntity = mapFileToSubmittedHtmlInput.get(key);
//			if (oldEntity != null && oldEntity != entity) {
//				mergeEntities(oldEntity, entity);
//			}
//			
//			mapFileToPhpRequestVariable.put(key, entity);
//			entity.setOnPage(null);
//		}
//	}
	}

}

package deprecated.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.iastate.analysis.references.DeclaringReference;
import edu.iastate.analysis.references.PhpRefToHtml;
import edu.iastate.analysis.references.Reference;
import edu.iastate.analysis.references.RegularReference;
import edu.iastate.symex.constraints.ConstraintFactory;

/**
 * EntityManager manages entities and references as follows.
 * 	+ entityList: contains all entities, each has one or more declaring references and a number of non-declaring references.
 *  + danglingReferenceList: contains dangling references (have no associated entity) 
 * 
 * EntityManager ensures that a non-dangling reference belongs to exactly one entity (ref.getEntity() != null),
 * and a dangling reference has no entity (ref.getEntity() == null).
 * 
 * A PHP Page may contain multiple references, and a reference may belong to multiple PHP pages, but that does not violate
 * the properties of EntityManager listed above.
 * 
 * @author HUNG
 *
 */
public class EntityManager {
	
	/**
	 * Maps a location (file and offset) to a reference
	 */
	private HashMap<File, HashMap<Integer, Reference>> mapLocationToReference = new HashMap<File, HashMap<Integer, Reference>>();
	
	/**
	 * List of entities
	 */
	private HashSet<Entity> entityList = new HashSet<Entity>();
	
	/**
	 * List of dangling references. Non-dangling references are managed by entityList.
	 */
	private HashSet<Reference> danglingReferenceList = new HashSet<Reference>();
	
	/**
	 * Maps a server page to a set of references detected from the server page. 
	 * Note that mapPageToReferences is different from mapLocationToReference:
	 * 		A server page x.php may contain a reference ref in file y.php
	 * 		=> mapPageToReference.get(x.php) & mapFileToReference.get(y.php) contain ref 
	 * 			whereas mapFileToReference(x.php) does not contain ref
	 */
	private HashMap<String, HashSet<Reference>> mapPageToReferences = new HashMap<String, HashSet<Reference>>();
	
	/*
	 * Update EntityManager
	 */
		
	/**
	 * Adds a list of references detected in a PHP page to EntityManager.
	 * @param phpPage	The file path of the PHP page
	 * @param referenceList
	 */
	public void addReferencesInPage(String phpPage, ArrayList<Reference> referenceList) {
		for (Reference reference : referenceList)
			addReferenceInPage(phpPage, reference);
	}
	
	/**
	 * Adds a reference detected in a PHP page to EntityManager.
	 * @param phpPage	The file path of the PHP page
	 * @param reference
	 */
	public void addReferenceInPage(String phpPage, Reference reference) {
		/*
		 * Update mapLocationToReference and mapPageToReferences
		 */
		if (!mapLocationToReference.containsKey(reference.getStartPosition().getFile()))
			mapLocationToReference.put(reference.getStartPosition().getFile(), new HashMap<Integer, Reference>());
		
		if (!mapPageToReferences.containsKey(phpPage))
			mapPageToReferences.put(phpPage, new HashSet<Reference>());
		
		
		HashMap<Integer, Reference> mapPositionToReference = mapLocationToReference.get(reference.getStartPosition().getFile());
		HashSet<Reference> referencesInPage = mapPageToReferences.get(phpPage);
		
		Reference existingReference = mapPositionToReference.get(reference.getStartPosition().getOffset());
		if (existingReference != null) {
			// The reference already exists.
			if (referencesInPage.contains(existingReference))
				return;
			else {
				// This is the case where a reference x is included in two different pages.
				if (existingReference.getConstraint() != reference.getConstraint())
					existingReference.setConstraint(ConstraintFactory.createOrConstraint(existingReference.getConstraint(), reference.getConstraint()));

				referencesInPage.add(existingReference);
				
				/*
				 * Update entityList and danglingReferenceList
				 */
				if (existingReference instanceof DeclaringReference) {
					updateLinksWithDeclaringReference(phpPage, (DeclaringReference) existingReference);
				}
				else {
					updateLinksWithRegularReference(phpPage, (RegularReference) existingReference);
				}
			}
		}
		else {
			// The reference is new.
			mapPositionToReference.put(reference.getStartPosition().getOffset(), reference);
			referencesInPage.add(reference);
			
			/*
			 * Update entityList and danglingReferenceList
			 */
			if (reference instanceof DeclaringReference) {
				Entity newEntity = new Entity((DeclaringReference) reference);
				linkEntityReference(newEntity, reference);
				entityList.add(newEntity);
				
				updateLinksWithDeclaringReference(phpPage, (DeclaringReference) reference);
			}
			else {
				reference.setEntity(null);
				danglingReferenceList.add(reference);
				
				updateLinksWithRegularReference(phpPage, (RegularReference) reference);
			}
		}
	}
	
	/**
	 * Updates entityList and danglingReferenceList with a DeclaringReference detected in a PHP page.
	 * @param phpPage	The file path of the PHP page 
	 * @param reference
	 */
	private void updateLinksWithDeclaringReference(String phpPage, DeclaringReference reference) {
		Entity curEntity = reference.getEntity();
		for (Entity entity : getEntitiesInPage(phpPage)) {
			if (entity != curEntity && canLinkEntities(entity, curEntity)) {
				MultiEntity multiEntity = linkEntities(entity, curEntity);
				entityList.remove(entity);
				entityList.remove(curEntity);
				entityList.add(multiEntity);
				
				curEntity = multiEntity;
				break;
			}
		}
		
		for (Reference danglingRef : getDanglingReferencesInPage(phpPage)) {
			if (canLinkEntityReference(curEntity, (RegularReference) danglingRef)) {
				linkEntityReference(curEntity, danglingRef);
				danglingReferenceList.remove(danglingRef);
			}
		}
	}
	
	/**
	 * Updates entityList and danglingReferenceList with a RegularReference detected in a PHP page.
	 * @param phpPage	The file path of the PHP page 
	 * @param reference
	 */
	private void updateLinksWithRegularReference(String phpPage, RegularReference reference) {
		for (Entity entity : getEntitiesInPage(phpPage)) {
			if (entity == reference.getEntity())
				continue;

			if (canLinkEntityReference(entity, reference)) {
				if (reference.getEntity() != null) {
					// If reference is currently linked with another entity, then merge the two entities.
					Entity curEntity = reference.getEntity();
					MultiEntity multiEntity = linkEntities(entity, curEntity);
					entityList.remove(entity);
					entityList.remove(curEntity);
					entityList.add(multiEntity);
				}
				else {
					// If reference is currently dangling, then link it to the entity.
					linkEntityReference(entity, reference);
					danglingReferenceList.remove(reference);
				}
				break;
			}
		}
	}
	
	/**
	 * Links references of type PhpRefToHtmlEntity to HTML entities from all over the system.
	 * This function should be called after all entities in the system have been collected.
	 * The reason is that a PhpRefToHtmlEntity can link to entities from anywhere.
	 */
	public void linkPhpRefsToHtmlEntities() {
		for (Reference reference : getReferenceList()) {
			if (!(reference instanceof PhpRefToHtml))
				continue;
			
			for (Entity entity : getEntityList()) {
				if (entity == reference.getEntity())
					continue;
			
				if (canLinkEntityReference(entity, (RegularReference) reference)) {
					if (reference.getEntity() != null) {
						// If reference is currently linked with another entity, then merge the two entities.
						Entity curEntity = reference.getEntity();
						MultiEntity multiEntity = linkEntities(entity, curEntity);
						entityList.remove(entity);
						entityList.remove(curEntity);
						entityList.add(multiEntity);
					}
					else {
						// If reference is currently dangling, then link it to the entity.
						linkEntityReference(entity, reference);
						danglingReferenceList.remove(reference);
					}
				}
				
				// No break statement here, continue doing for other entities.
			}
		}
	}
	
	/**
	 * Removes all references detected in a PHP page from EntityManager.
	 *   This method is not reliable and should be used with care. The reason
	 * is that an entity can have references in different pages, removing a reference
	 * in one page affects the others.
	 * @param phpPage	The file path of the PHP page 
	 */
	public void removeReferencesInPage(String phpPage) {
		for (Entity entity : getEntitiesInPage(phpPage)) {
			for (Reference reference : entity.getReferences()) {
				HashMap<Integer, Reference> mapPositionToReference = mapLocationToReference.get(reference.getStartPosition().getFile());
				mapPositionToReference.remove(reference.getStartPosition().getOffset());
			}
			entityList.remove(entity);
		}
		
		for (Reference danglingRef : getDanglingReferencesInPage(phpPage)) {
			HashMap<Integer, Reference> mapPositionToReference = mapLocationToReference.get(danglingRef.getStartPosition().getFile());
			mapPositionToReference.remove(danglingRef.getStartPosition().getOffset());
			danglingReferenceList.remove(danglingRef);
		}
		
		mapPageToReferences.put(phpPage, new HashSet<Reference>());
	}
	
	/*
	 * Get properties
	 */
	
	public ArrayList<Reference> getReferenceList() {
		ArrayList<Reference> referenceList = new ArrayList<Reference>();
		for (HashMap<Integer, Reference> mapPositionToReferece : mapLocationToReference.values()) {
			referenceList.addAll(mapPositionToReferece.values());
		}
		return referenceList;
	}
	
	public ArrayList<Entity> getEntityList() {
		return new ArrayList<Entity>(entityList);
	}
	
	public ArrayList<Reference> getDanglingReferenceList() {
		return new ArrayList<Reference>(danglingReferenceList);
	}
	
	/**
	 * Returns the references located in a file.
	 * @see entities.EntityManager.getReferencesInPage(String)
	 */
	public ArrayList<Reference> getReferencesInFile(String relativeFilePath) {
		ArrayList<Reference> referenceList = new ArrayList<Reference>();
		if (mapLocationToReference.containsKey(relativeFilePath)) {
			referenceList.addAll(mapLocationToReference.get(relativeFilePath).values());
		}
		return referenceList;
	}
	
	/**
	 * Returns the entities that have at least one reference located in a file.
	 * @see entities.EntityManager.getEntitiesInPage(String)
	 */
	public ArrayList<Entity> getEntitiesInFile(String relativeFilePath) {
		HashSet<Entity> entityList = new HashSet<Entity>();
		for (Reference reference : getReferencesInFile(relativeFilePath)) {
			if (reference.getEntity() != null)
				entityList.add(reference.getEntity());
		}
		return new ArrayList<Entity>(entityList);
	}
	
	/**
	 * Returns the dangling references located in a file.
	 * @see entities.EntityManager.getDanglingReferencesInPage(String)
	 */
	public ArrayList<Reference> getDanglingReferencesInFile(String relativeFilePath) {
		ArrayList<Reference> referenceList = new ArrayList<Reference>();
		for (Reference reference : getReferencesInFile(relativeFilePath)) {
			if (reference.getEntity() == null)
				referenceList.add(reference);
		}
		return referenceList;
	}
	
	/**
	 * Returns the references detected when executing a PHP page.
	 * Note that the references may be located in a different file than the PHP page.
	 * @param phpPage
	 * @see entities.EntityManager.getReferencesInFile(String)
	 */
	public ArrayList<Reference> getReferencesInPage(String phpPage) {
		ArrayList<Reference> referenceList = new ArrayList<Reference>();
		if (mapPageToReferences.containsKey(phpPage))
			referenceList.addAll(mapPageToReferences.get(phpPage));
		return referenceList;
	}
	
	/**
	 * Returns the entities detected when executing a PHP page.
	 * Note that the entities may be located in a different file than the PHP page.
	 * @param phpPage
	 * @see entities.EntityManager.getEntitiesInFile(String)
	 */
	public ArrayList<Entity> getEntitiesInPage(String phpPage) {
		HashSet<Entity> entityList = new HashSet<Entity>();
		for (Reference reference : getReferencesInPage(phpPage)) {
			if (reference.getEntity() != null)
				entityList.add(reference.getEntity());
		}
		return new ArrayList<Entity>(entityList);
	}
	
	/**
	 * Returns the dangling references detected when executing a PHP page.
	 * Note that the references may be located in a different file than the PHP page.
	 * @param phpPage
	 * @see entities.EntityManager.getDanglingReferencesInFile(String)
	 */
	public ArrayList<Reference> getDanglingReferencesInPage(String phpPage) {
		ArrayList<Reference> referenceList = new ArrayList<Reference>();
		for (Reference reference : getReferencesInPage(phpPage)) {
			if (reference.getEntity() == null)
				referenceList.add(reference);
		}
		return referenceList;
	}
	
	/**
	 * Returns the list of entities with a special entity (UndeclaredEntity)
	 * containing all dangling references.
	 */
	public ArrayList<Entity> getEntityListIncludingDanglingRefs() {
		ArrayList<Entity> entityList = getEntityList();
		
		UndeclaredEntity undeclaredEntity = new UndeclaredEntity(); // UnclaredEntity is used to display dangling references
		for (Reference reference : getDanglingReferenceList()) {
			undeclaredEntity.addReference(reference); // Do not set reference.setEntity(unclaredEntity),
													  // so that a danglingReference's entity is always null.
		}
		if (!undeclaredEntity.getReferences().isEmpty()) {
			entityList.add(undeclaredEntity);
		}
		
		return entityList;
	}
	
	/**
	 * Determines whether two entities can be linked together to make a MultiEntity.
	 */
	public static boolean canLinkEntities(Entity entity1, Entity entity2) {
		return entity1.getDeclaringReference().sameAs(entity2.getDeclaringReference());
	}
	
	/**
	 * Links two entities together to make a MultiEntity. 
	 */
	public static MultiEntity linkEntities(Entity entity1, Entity entity2) {
		MultiEntity multiEntity = new MultiEntity(entity1, entity2);
		for (Reference reference : entity1.getReferences()) {
			unlinkEntityReference(entity1, reference);
			linkEntityReference(multiEntity, reference);
		}
		for (Reference reference : entity2.getReferences()) {
			unlinkEntityReference(entity2, reference);
			linkEntityReference(multiEntity, reference);
		}
		return multiEntity;
	}
	
	/**
	 * Determines whether a reference refers to an entity.
	 * @param entity
	 * @param reference
	 * @return True if the reference refers to the entity
	 */
	public static boolean canLinkEntityReference(Entity entity, RegularReference reference) {
		// TODO
		return reference.refersTo(entity.getDeclaringReference())
				//&& (WebEntitiesConfig.DISCARD_CONSTRAINTS_WHEN_COMPARING_ENTITIES
				&& (false
						|| reference instanceof PhpRefToHtml 	// Don't consider constraints for PhpRefToHtmlEntity
						|| reference.getConstraint().satisfies(entity.getConstraint())); 
	}
	
	/**
	 * Links an entity and a reference.
	 * @param entity
	 * @param reference
	 */
	public static void linkEntityReference(Entity entity, Reference reference) {
		entity.addReference(reference);
		reference.setEntity(entity);
	}
	
	/**
	 * Unlinks an entity and a reference.
	 * @param entity
	 * @param reference
	 */
	public static void unlinkEntityReference(Entity entity, Reference reference) {
		entity.removeReference(reference);
		reference.setEntity(null);
	}
	
}

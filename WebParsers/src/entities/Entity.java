package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import constraints.Constraint;
import deprecated.WebEntitiesConfig;
import references.DeclaringReference;
import references.Reference;

/**
 * 
 * @author HUNG
 *
 */
public class Entity {
	
	protected DeclaringReference declaringReference;
	protected ArrayList<Reference> references = new ArrayList<Reference>(); // references should include declaringReference
	
	/**
	 * Constructor. The entity must be linked to the reference after this constructor is called.
	 * @param declaringReference
	 */
	public Entity(DeclaringReference declaringReference) {
		this.declaringReference = declaringReference;
	}
	
	/*
	 * Set properties
	 */
	
	/**
	 * Adds a reference.
	 * @param reference
	 */
	public void addReference(Reference reference) {
		references.add(reference);
	}
	
	/**
	 * Removes a reference
	 * @param reference
	 */
	public void removeReference(Reference reference) {
		references.remove(reference);
	}
	
	/*
	 * Get properties
	 */
	
	public DeclaringReference getDeclaringReference() {
		return declaringReference;
	}
	
	public ArrayList<Reference> getReferences() {
		return new ArrayList<Reference>(references);
	}
	
	public String getName() {
		return declaringReference.getName();
	}
	
	public String getType() {
		return declaringReference.getType().replace("Decl", "");
	}
	
	/**
	 * Returns the file path of the declaring reference
	 */
	public String getFilePath() {
		return declaringReference.getFilePath();
	}
	
	/**
	 * Returns the position of the declaring reference
	 */
	public int getPosition() {
		return declaringReference.getPosition();
	}
	
	/**
	 * Returns the constraint of the declaring reference of this entity.
	 */
	public Constraint getConstraint() {
		return declaringReference.getConstraint();
	}
	
	/*
	 * Supports sorting of entities
	 */
	
	public static class EntityComparator implements Comparator<Entity> {
		
		private Comparator<Entity> firstComparator, secondComparator, thirdComparator;
		
		public EntityComparator(Comparator<Entity> firstComparator, Comparator<Entity> secondComparator, Comparator<Entity> thirdComparator) {
			this.firstComparator = firstComparator;
			this.secondComparator = secondComparator;
			this.thirdComparator = thirdComparator;
		}
		
		public EntityComparator() {
			this(new EntityComparatorByName(), new EntityComparatorByType(), new EntityComparatorByFile());
		}

		@Override
		public int compare(Entity ent1, Entity ent2) {
			int result = firstComparator.compare(ent1, ent2);
			if (result != 0)
				return result;
			
			result = secondComparator.compare(ent1, ent2);
			if (result != 0)
				return result;
			
			result = thirdComparator.compare(ent1, ent2);
			return result;
		}
	}
	
	public static class EntityComparatorByName implements Comparator<Entity> {

		@Override
		public int compare(Entity ent1, Entity ent2) {
			return ent1.getName().compareTo(ent2.getName());
		}
	}
	
	public static class EntityComparatorByType implements Comparator<Entity> {

		@Override
		public int compare(Entity ent1, Entity ent2) {
			return ent1.getType().compareTo(ent2.getType());
		}
	}
	
	public static class EntityComparatorByFile implements Comparator<Entity> {

		@Override
		public int compare(Entity ent1, Entity ent2) {
			return ent1.getFilePath().compareTo(ent2.getFilePath());
		}
	}
	
	/**
	 * Prints the entity to an XML element
	 */
	public Element printToXmlElement(Document document) {
		Collections.sort(references, new Reference.ReferenceComparator()); // Sort the references first
		
		Element entityElement = document.createElement(WebEntitiesConfig.XML_ENTITY);
		entityElement.setAttribute(WebEntitiesConfig.XML_ENT_NAME, this.getName());
		entityElement.setAttribute(WebEntitiesConfig.XML_ENT_TYPE, this.getType());
		
		entityElement.setAttribute(WebEntitiesConfig.XML_FILE_PATH, this.getFilePath());
		for (Reference reference : references) {
			entityElement.appendChild(reference.printToXmlElement(document));
		}
		
		return entityElement;
	}
	
	/**
	 * Reads the entity from an XML element
	 */
	public static Entity readEntityFromXmlElement(Element entityElement) {
		return null;
//		Type entityType = Type.valueOf(entityElement.getAttribute(WebEntitiesConfig.XML_REF_TYPE));
//		String entityName = entityElement.getAttribute(WebEntitiesConfig.XML_REF_NAME);
//		Entity entity = new Entity(entityType, entityName, entityName); // Now it's Ok to discard the qualifiedName
//		
//		if (entityElement.hasAttribute(WebEntitiesConfig.XML_ENTITY_INFO1))
//			entity.submitToPage = entityElement.getAttribute(WebEntitiesConfig.XML_ENTITY_INFO1);
//		if (entityElement.hasAttribute(WebEntitiesConfig.XML_REF_INFO2))
//			entity.onPage = entityElement.getAttribute(WebEntitiesConfig.XML_REF_INFO2);
//		
//		NodeList nodeList = entityElement.getChildNodes();
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			Element referenceElement = (Element) nodeList.item(i);
//			Reference.readReferenceFromXmlElement(referenceElement, entity);
//			EntityManager.linkEntityReference(entity, reference);
//		}
//		return entity;
	}
	
}

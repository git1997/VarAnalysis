package references;

import sourcetracing.Location;

/**
 * 
 * @author HUNG
 *
 */
public abstract class DeclaringReference extends Reference {

	public DeclaringReference(String name, Location location) {
		super(name, location);
	}
	
}

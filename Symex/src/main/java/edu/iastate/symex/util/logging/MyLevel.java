package edu.iastate.symex.util.logging;

import java.util.logging.Level;

/**
 * 
 * @author HUNG
 *
 */
public class MyLevel extends Level {

	private static final long serialVersionUID = 13142558501089071L;
	
	public static final Level PROGRESS		 = new MyLevel("Progress", 5);
	public static final Level JAVA_EXCEPTION = new MyLevel("JavaException", 4);
    public static final Level USER_EXCEPTION = new MyLevel("UserException", 3);
    public static final Level TODO			 = new MyLevel("Todo", 2);
    public static final Level INFO			 = new MyLevel("Info", 1);

	public MyLevel(String name, int value) {
		super(name, value);
	}
	
}

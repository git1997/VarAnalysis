package edu.iastate.symex.debug;

/**
 * 
 * @author HUNG
 *
 */
public class DebugInfo {
	
	private Trace trace;

	/**
	 * Protected constructor, called from Debugger only.
	 */
	protected DebugInfo() {
		trace = null;
	}
	
	/**
	 * Protected method, called from Debugger only.
	 */
	protected void setTrace(Trace trace) {
		this.trace = trace;
	}
	
	/**
	 * Returns the trace
	 */
	public Trace getTrace() {
		return trace;
	}

}

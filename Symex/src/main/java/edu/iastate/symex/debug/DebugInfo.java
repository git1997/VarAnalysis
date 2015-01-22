package edu.iastate.symex.debug;

import edu.iastate.symex.datamodel.DataModel;

/**
 * 
 * @author HUNG
 *
 */
public class DebugInfo {
	
	private DataModel dataModel;
	private Trace trace;

	/**
	 * Protected constructor, called from Debugger only.
	 */
	protected DebugInfo() {
		dataModel = null;
		trace = null;
	}
	
	/**
	 * Protected method, called from Debugger only.
	 */
	protected void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}
	
	/**
	 * Protected method, called from Debugger only.
	 */
	protected void setTrace(Trace trace) {
		this.trace = trace;
	}
	
	/**
	 * Returns the dataModel
	 */
	public DataModel getDataModel() {
		return dataModel;
	}
	
	/**
	 * Returns the trace
	 */
	public Trace getTrace() {
		return trace;
	}

}

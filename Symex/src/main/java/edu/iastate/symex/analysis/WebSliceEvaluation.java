package edu.iastate.symex.analysis;

/**
 * 
 * @author HUNG
 *
 */
public class WebSliceEvaluation {
	
	public static IListener listener = null;
	
	/*
	 * Interfaces
	 */
	
	public interface IListener {
		
		public void onStatementExecute();
		
	}
	
	/*
	 * Methods
	 */
	
	public static void onStatementExecute() {
		if (listener != null)
			listener.onStatementExecute();
	}
	
	/*
	 * TODO Add the following code to edu.iastate.symex.php.nodes.BlockNode
	 */
//	/*
//	 * WebSlice Evaluation
//	 */
//	WebSliceEvaluation.onStatementExecute();
	
}

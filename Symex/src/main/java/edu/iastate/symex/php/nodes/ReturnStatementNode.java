package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;

import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.SpecialNode;

/**
 * 
 * @author HUNG
 *
 */
public class ReturnStatementNode extends StatementNode {
	
	private ExpressionNode expression;
	
	/*
	Represent a return statement 

	e.g. return;
	 return $a;
	*/
	public ReturnStatementNode(ReturnStatement returnStatement) {
		super(returnStatement);
		expression = (returnStatement.getExpression() != null ? ExpressionNode.createInstance(returnStatement.getExpression()) : null);
	}
	
	@Override
	public DataNode execute(Env env) {
		if (expression != null) {
			DataNode returnValue = expression.execute(env);
			env.addReturnValue(returnValue);
			
			/*
			 * The following code is used for web analysis. Comment out/Uncomment out if necessary.
			 */
			// BEGIN OF WEB ANALYSIS CODE
			if (WebAnalysis.entityDetectionListener != null)
				WebAnalysis.onReturnStatementExecute((ReturnStatement) this.getAstNode(), env);
			// END OF WEB ANALYSIS CODE
		}		
		return SpecialNode.ControlNode.RETURN;
	}

}
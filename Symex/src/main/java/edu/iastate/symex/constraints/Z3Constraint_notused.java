//package edu.iastate.symex.constraints;
//package edu.iastate.symex.constraints;
//
//import com.microsoft.z3.BoolExpr;
//import com.microsoft.z3.Solver;
//import com.microsoft.z3.Status;
//import com.microsoft.z3.Z3Exception;
//
///**
// * 
// * @author HUNG
// *
// */
//public class Z3Constraint extends Constraint {
//	
//	// The FeatureExpr representing this constraint
//	protected BoolExpr featureExpr;
//	
//	/**
//	 * Protected constructor. Called from Z3ConstraintFactory only.
//	 * @param featureExpr
//	 */
//	protected Z3Constraint(BoolExpr featureExpr) {
//		this.featureExpr = featureExpr;
//	}
//	
//	/*
//	 * Methods
//	 */
//	
//	/**
//	 * Returns the FeatureExpr representing this constraint.
//	 */
//	public BoolExpr getFeatureExpr() {
//		return featureExpr;
//	}
//
//	@Override
//	public String toDebugString() {
//		return featureExpr.toString();
//	}
//	
//	@Override
//	public boolean isSatisfiable() {
//		try {
//			Solver s = Z3ConstraintFactory.ctx.MkSolver();
//	        s.Assert(featureExpr);
//	        return (s.Check() == Status.SATISFIABLE);
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//}

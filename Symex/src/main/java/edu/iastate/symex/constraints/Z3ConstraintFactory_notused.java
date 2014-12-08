//package edu.iastate.symex.constraints;
//package edu.iastate.symex.constraints;
//
//import com.microsoft.z3.BoolExpr;
//import com.microsoft.z3.Context;
//import com.microsoft.z3.Z3Exception;
//
///**
// * 
// * @author HUNG
// *
// */
//public class Z3ConstraintFactory extends ConstraintFactory {
//
//	protected static Context ctx;
//	
//	static {
//		try {
//			ctx = new Context();
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	protected Constraint createTrue() {
//		try {
//			return new Z3Constraint(ctx.MkBool(true));
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	protected Constraint createFalse() {
//		try {
//			return new Z3Constraint(ctx.MkBool(false));
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	protected Constraint createAtomic(String conditionString) {
//		try {
//			return new Z3Constraint(ctx.MkBoolConst(conditionString));
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	protected Constraint createAnd(Constraint constraint1, Constraint constraint2) {
//		try {
//			return new Z3Constraint(ctx.MkAnd(new BoolExpr[]{((Z3Constraint) constraint1).featureExpr, ((Z3Constraint) constraint2).featureExpr}));
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	protected Constraint createOr(Constraint constraint1, Constraint constraint2) {
//		try {
//			return new Z3Constraint(ctx.MkOr(new BoolExpr[]{((Z3Constraint) constraint1).featureExpr, ((Z3Constraint) constraint2).featureExpr}));
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Override
//	protected Constraint createNot(Constraint oppositeConstraint) {
//		try {
//			return new Z3Constraint(ctx.MkNot(((Z3Constraint) oppositeConstraint).featureExpr));
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//}

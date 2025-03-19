package fsb.ast;


import java.util.List;
import java.util.Set;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.State;
import fsb.semantics.Semantics;
import fsb.tvl.BoolValue;
import fsb.utils.Options;

public abstract class BoolExpr {
	public enum BXType {
		EQUAL {
			@Override public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {return ctx.mkEq(leftZ3, rightZ3);}
			@Override public String toString(){return "=";}}, 
		NEQ{
			@Override public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {return ctx.mkNot(ctx.mkEq(leftZ3, rightZ3));}
			@Override public String toString(){return "!=";}},
		GREATER{
			@Override public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {return ctx.mkGt(leftZ3, rightZ3);}
			@Override public String toString(){return ">";}}, 
		LESS{
			@Override public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {return ctx.mkLt(leftZ3, rightZ3);}
			@Override public String toString(){return "<";}}, 
		AND{
			@Override public com.microsoft.z3.Expr toZ3(Context ctx, com.microsoft.z3.BoolExpr leftZ3, com.microsoft.z3.BoolExpr rightZ3){return ctx.mkAnd(leftZ3, rightZ3);}
			@Override public String toString(){return "&&";}}, 
		OR{
			@Override public com.microsoft.z3.Expr toZ3(Context ctx, com.microsoft.z3.BoolExpr leftZ3, com.microsoft.z3.BoolExpr rightZ3){return ctx.mkOr(leftZ3, rightZ3);}
			@Override public String toString(){return "||";}}, 
		NOT{
				@Override public String toString(){return "!";}}, 
		LE{ 
			@Override public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {return ctx.mkLe(leftZ3, rightZ3);}
			@Override public String toString(){return "<=";}}, 
		GE{ 
			@Override public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {return ctx.mkGe(leftZ3, rightZ3);}
			@Override public String toString(){return ">=";}}, 
		IFF{
			@Override public com.microsoft.z3.Expr toZ3(Context ctx, com.microsoft.z3.BoolExpr leftZ3, com.microsoft.z3.BoolExpr rightZ3){return ctx.mkIff(leftZ3, rightZ3);}
			@Override public String toString(){return "iff";}};

		public abstract String toString();

		/**
		 * @param ctx
		 * @param leftZ3
		 * @param rightZ3
		 * @return
		 */
		public com.microsoft.z3.Expr toZ3(Context ctx, com.microsoft.z3.BoolExpr leftZ3, com.microsoft.z3.BoolExpr rightZ3) {
			throw new IllegalStateException("Invalid type for complex boolean expression!");
		}

		/**
		 * @param ctx
		 * @param leftZ3
		 * @param rightZ3
		 * @return
		 */
		public com.microsoft.z3.BoolExpr toZ3(Context ctx, ArithExpr leftZ3, ArithExpr rightZ3) {
			throw new RuntimeException("Invalid type for comparison boolean expression!");
		}
	};
	abstract protected BoolValue evaluate_inner(State s, int pid); 
	abstract public List<Integer> getVariablesInvolved();

	/**
	 * This function assumes the expression is in DNF form(or negated DNF)
	 */
	abstract public Set<BoolExpr> allSatisfiedCubes(State s, int pid);
	/**
	 * This function assumes the expression is in DNF form(or negated DNF)
	 */
	abstract public Set<BoolExpr> allNegationSatisfiedCubes(State s, int pid);
	/**
	 * This function assumes the expression is in DNF form(or negated DNF)
	 */
	abstract public Set<BoolExpr> allCubes();

	/**
	 * @return the Z3 boolean expression corresponding to this fsb.ast.BoolExpr
	 * @throws Exception 
	 */
	abstract public com.microsoft.z3.BoolExpr toZ3(Context ctx);


	final public BoolValue evaluate(State s, int pid) {
		BoolValue ret = evaluate_inner(s, pid);
		if (Options.PRINT_USED_CUBES){
			ComplexBool.m_allCubesGlobal.addAll(allCubes());
			//if undetermined condition not interesting
			if(ret.isTrue()){
				Semantics.addCubesToSet(allSatisfiedCubes(s,pid),
						allCubes(),Semantics.cubesTrueTogetherMap);
				ComplexBool.m_usefullCubesGlobal.addAll(allSatisfiedCubes(s,pid));
			}
			if(ret.isFalse()){
				Semantics.addCubesToSet(allNegationSatisfiedCubes(s,pid),
						allCubes(),Semantics.cubesFalseTogetherMap);
				ComplexBool.m_usefullCubesGlobal.addAll(allNegationSatisfiedCubes(s,pid));
			}

		}
		return ret;
	}

}

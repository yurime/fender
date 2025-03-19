/**
 * 
 */
package fsb.extrapolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.InterpolationContext;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import fsb.ast.SymbolTable;
import fsb.explore.Action;
import fsb.explore.ExprValidator;
import fsb.explore.SBState;
import fsb.explore.State;
import fsb.explore.Validator;
import fsb.semantics.sc.SCState;
import fsb.z3.misc.FilterPredicates;

/**
 * @author yurime
 *
 */
public class Interpolate extends IGuessPredicates {

	@Override
		protected List<Expr> guessZ3ExprPredicates(ArrayList<fsb.ast.BoolExpr> predicates, ArrayList<Action> concrete_trace) {
			System.out.println("Starting Inerpolate::execute()");
	
			InterpolationContext ctx = initZ3();
			
			List<BoolExpr> formulas = traceToZ3Formulas(concrete_trace, ctx, predicates);
					
			HashSet<BoolExpr> predicates_ret = pathInterpolation(ctx, formulas);
			com.microsoft.z3.BoolExpr[] aux = new com.microsoft.z3.BoolExpr[1];
						
			List<Expr> result = Z3toStreamFlatten(predicates_ret.toArray(aux)).collect(Collectors.toList());
			System.out.println(" recieved:\n" + result);
			
			List<com.microsoft.z3.BoolExpr> predicates_z3 = predicates.stream().map(x->x.toZ3(ctx)).collect(Collectors.toList());
			return FilterPredicates.filterPredicates(predicates_z3,  result, ctx);


		}


	/**
	 * splits the formulas into two parts A and B in changing locations to do interpolations
	 * @param ctx
	 * @param formulas
	 * @return
	 */
	private HashSet<BoolExpr> pathInterpolation(InterpolationContext ctx, List<BoolExpr> formulas) {
		List<BoolExpr> formulas_A = new LinkedList<>();
		List<BoolExpr> formulas_B = formulas;
		com.microsoft.z3.BoolExpr[] aux = new com.microsoft.z3.BoolExpr[1];
		HashSet<BoolExpr> predicates_ret = new HashSet<>();


		System.out.println("Got formulas:\n" + formulas_B);
		
		//lower bound 1 and not zero to exclude Interp(A,B) for an empty formula.
		while(formulas_B.size() > 1){
		    //System.out.println("formulas_A.size(): " + formulas_A.size());
		    
			formulas_A.add(formulas_B.remove(0));
			BoolExpr A = ctx.mkAnd(formulas_A.toArray(aux));
			BoolExpr B = ctx.mkAnd(formulas_B.toArray(aux));
		    HashSet<BoolExpr> interps = classicalInterpolation(ctx, A, B);
		    
			if(interps.contains(ctx.mkTrue())){
			    System.out.println("Interpolant: TRUE");
				continue;
			}
			if(interps.contains(ctx.mkFalse())){
			    System.out.println("Interpolant: FALSE");
			    predicates_ret.addAll(pathInterpolation(ctx, formulas_A));
				break;
			}
			 predicates_ret.addAll(interps);
		    
		}
		return predicates_ret;
	}

	/**
	 * classical interpolation of two formulas A and B
	 * @param ctx
	 * @param formulas_A
	 * @param formulas_B
	 * @param aux
	 */
	private HashSet<BoolExpr> classicalInterpolation(InterpolationContext ctx, BoolExpr A, BoolExpr B) {

		HashSet<BoolExpr> ret = new HashSet<>();
		
		Solver s = ctx.mkSolver();		
		s.add(A);
		s.add(B);
		Status is_sat = s.check();
		System.out.println("check satisfiability: " + is_sat);
		if(is_sat == Status.SATISFIABLE) throw new RuntimeException("the error trace translated to z3 is satisfable\n"
																	+ "possible cases:\n"
																	+ "1. Predicates set insufficient to capture program assertion"
																	+ "2. non-spurious error trace:"
																	+ s.getModel());
		

		Expr proof = s.getProof();   
				    
		BoolExpr iA = ctx.MkInterpolant(A);//Create an expression that marks a formula position for interpolation.
		BoolExpr pat = ctx.mkAnd(iA, B);
		
		Params params = ctx.mkParams();
		
		BoolExpr[] interps = ctx.GetInterpolant(proof, pat, params);

		for(BoolExpr i : interps){
		    System.out.println("Interpolant: " + i);
		    ret.add( filterNewInstanceSep(ctx, i)  );
		}
		return ret;
	}

	/**
	 * removing from expression new instances of variable<br>
	 * and replacing them with the original variable (e.g. x@1,x@2--> x)
	 * @param ctx
	 * @param p
	 * @return
	 */
	private BoolExpr filterNewInstanceSep(InterpolationContext ctx, BoolExpr p) {
		BoolExpr new_pred = p;
		for(Expr from : getZ3ExprVars(p) ){
				String var_name = from.toString();
				if(!var_name.contains(ActionToZ3BoolWithRenaming.N_I_SEP)) continue;
				String new_name  = var_name.substring(0, var_name.indexOf(ActionToZ3BoolWithRenaming.N_I_SEP));
				ArithExpr to = ctx.mkIntConst(new_name);
				new_pred = (BoolExpr) new_pred.substitute(from, to);
		}
		return new_pred;
	}
    /**
	 * helper to {@link #traceToZ3Formulas}
	 * @param ctx
	 * @param predicates
	 * @param varToInstance
	 * @return
	 */
	public com.microsoft.z3.BoolExpr finalStateToZ3Bool(Context ctx, ArrayList<fsb.ast.BoolExpr> predicates,
			HashMap<Integer, ArrayList<Integer>> varToInstance) {

		Validator v = State.program.getValidator();
		if (! (v instanceof ExprValidator) ) {
			throw new IllegalStateException("expected ExprValidator as the assertion, and got " + v);
		}
		com.microsoft.z3.BoolExpr finalAssertion = ((ExprValidator)v).toZ3(ctx, predicates);
		
		// Add negation of the assertion to the final state formula 
		
		System.out.println("finalAssertion: ");
		System.out.println(" " + finalAssertion);
		for(int local : varToInstance.keySet()){
			ArrayList<Integer> mapping = varToInstance.get(local);
			ArithExpr from = ctx.mkIntConst(SymbolTable.getLocalVariableName(local));
			int new_local = ((mapping.size() > 0)? mapping.get(mapping.size() - 1) : local);
			ArithExpr to = ctx.mkIntConst(SymbolTable.getLocalVariableName(new_local));
			
			finalAssertion = (com.microsoft.z3.BoolExpr)finalAssertion.substitute(from, to);
		}
		return finalAssertion;
	}
	

	/**inits Z3 and @returns the new z3 context
	 */
	@Override
	protected InterpolationContext initZ3() {
		HashMap<String, String> cfg = new HashMap<String, String>();
	    cfg.put("model", "true");
	    InterpolationContext ctx = new InterpolationContext(cfg);
		return ctx;
	}



	
	protected ArrayList<Expr> getZ3ExprVars(Expr e){
		ArrayList<Expr> ret = new ArrayList<>();
		if (e.isVar() || e.isConst()) {
			   ret.add(e);
		}else {
			for (Expr s_e : e.getArgs()) {
		        ret.addAll(getZ3ExprVars(s_e));
		    }
		}
		return ret;
	}

	
    /**
     * transformation of the trace from fender before doing Interpolation <br>
     * <br>
     * A note about the pc. Assuming that the error trace ends with the final error stateit either ends in the state of the error 
     * or the pc is not relevant to the detected error.
     * If want to include pc it should be included as an assignment.
     * @param predicates
     * @return
     */
	private List<BoolExpr> traceToZ3Formulas(ArrayList<Action> concrete_trace, Context ctx, ArrayList<fsb.ast.BoolExpr> predicates) {


		ArrayList<String> vars = new ArrayList<String>();	
		HashMap<Integer,ArrayList<Integer>> varToInstance = new HashMap<Integer, ArrayList<Integer>>();
		
		//addPcAsVars(concrete_trace, vars, varToInstance);

		List<BoolExpr> formulas = new LinkedList<>();
		
		for(int index=0; index < concrete_trace.size(); ++index){
			
			Action action = concrete_trace.get(index);
			
			BoolExpr action_z3 = ActionToZ3BoolWithRenaming.transform(ctx, predicates, vars, varToInstance, action.getStatement());
			
			//ArrayList<BoolExpr> pc_s = collectPCsFromAction(ctx, varToInstance, action);
			
			//action_z3 = ctx.mkAnd(action_z3, ctx.mkAnd(pc_s.toArray(new BoolExpr[1])));
			
			formulas.add(action_z3);
			
		}	
		com.microsoft.z3.BoolExpr finalStateZ3 = finalStateToZ3Bool(ctx, predicates, varToInstance);
		formulas.add(finalStateZ3);
		return formulas;
	}

	


	
}

/*
[(overflow == 0), (head_cnt_p1 == 0), (head_cnt_p1 == 1), (head_cnt_p1 == 2), (h1 == head), (h1 == head_1_p1), (h1 == head_2_p1), (h2 == head), (h2 == head_1_p1), (h2 == head_2_p1), (tail <= h2), (tail < head), (tail < head_1_p1), (tail < head_2_p1), (tail <= head), (tail <= head_1_p1), (tail <= head_2_p1), (tail <= h1)]
*/
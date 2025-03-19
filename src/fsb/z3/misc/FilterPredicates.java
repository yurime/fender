package fsb.z3.misc;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class FilterPredicates {
	
	
	
	public static List<Expr>  filterPredicates(List<BoolExpr> predicates, List<Expr> deduced_predicates, Context ctx){
		return filterEquivalent(predicates, deduced_predicates, ctx);
	}
	/**
	 * Implementation pondering: a predicate b is to be added if:<br>
	 * And.(not(existing_b -> b)) is satisfiable<br>
	 * or if<br>
	 * Or.(existing_b -> b) is unsatisfiable add<br>
	 * The first one has a caveat.. we get: <br>
	 * (not (e_b1->b)) and (not (e_b2->b)) not satisfiable, but<br>
	 * (not (e_b1->b)) satisfiable and (not (e_b2->b)) satisfiable.
	 *  so, we need to look if each one separatly is satisfiable.<br>
	 *  <br>
	 *  For the second one, sat means there is an assignment for which
	 *  (e_b1->b). For instance (x>3) will imply (x>4) for x=5. <br>
	 *  So, the second one seems non-applicable. If it is, both options should be implemented. 
	 * @param predicates
	 * @param deduced_predicates
	 * @param ctx
	 * @return
	 */
		public static List<Expr> filterImplied(List<BoolExpr> predicates, List<Expr> deduced_predicates, Context ctx) {
			System.out.println("Starting filterImplied");
			List<Expr> ret = new ArrayList<Expr>(predicates);
			for(Expr b_expr : deduced_predicates){
				BoolExpr b = (BoolExpr)b_expr;
				// to minimize z3 calls asking for all predicates together first
				BoolExpr e = ctx.mkTrue();
				for(Expr existing_b_expr : ret){
					BoolExpr existing_b = (BoolExpr)existing_b_expr;
					e = ctx.mkAnd(e, buildIsNotImplied(ctx, b, existing_b));
					e = ctx.mkAnd(e, buildIsNotImplied(ctx, ctx.mkNot(b), existing_b));
				}
				Solver s = ctx.mkSolver();
				s.add(e);
				if(s.check() == Status.SATISFIABLE){
					ret.add(b);
				}else{
					// looking at each predicate separately
					boolean not_implied = true;
					for(Expr existing_b_expr : ret){
						BoolExpr existing_b = (BoolExpr)existing_b_expr;
						e = buildIsNotImplied(ctx, b, existing_b);
						e = buildIsNotImplied(ctx, ctx.mkNot(b), existing_b);
						Solver s2 = ctx.mkSolver();
						s2.add(e);
						if(s2.check() == Status.SATISFIABLE){
							continue;
						}else{
							not_implied = false;
							//checking if e.g. x<3 -> x<=3 in which case, 
							// add if x<3 is already in preds
							if(predicates.contains(existing_b) && !predicates.contains(b)){
								not_implied = true;
							}
							System.out.println("not SAT" + s2.toString());						
						}
					}
					if(not_implied){
						ret.add(b);
					}
				}
			}	
			System.out.println(" returning:\n" + ret);

			return ret;
		}
		
		/**

		 * @param predicates
		 * @param deduced_predicates
		 * @param ctx
		 * @return
		 */
			public static List<Expr> filterEquivalent(List<BoolExpr> predicates, List<Expr> deduced_predicates, Context ctx) {
				System.out.println("Starting filterEquivalent");

				List<Expr> ret = new ArrayList<Expr>(predicates);
				for(Expr b_expr : deduced_predicates){
					BoolExpr b = (BoolExpr)b_expr;
					// to minimize z3 calls asking for all predicates together first
					BoolExpr e = ctx.mkTrue();
					for(Expr existing_b_expr : ret){
						BoolExpr existing_b = (BoolExpr)existing_b_expr;
						e = ctx.mkAnd(e, buildIsNotEquivalent(ctx, b, existing_b));
						e = ctx.mkAnd(e, buildIsNotEquivalent(ctx, ctx.mkNot(b), existing_b));
					}
					Solver s = ctx.mkSolver();
					s.add(e);
					if(s.check() == Status.SATISFIABLE){
						ret.add(b);
					}else{
						// looking at each predicate separately
						boolean not_implied = true;
						for(Expr existing_b_expr : ret){
							BoolExpr existing_b = (BoolExpr)existing_b_expr;
							BoolExpr set[] = {existing_b, ctx.mkNot(existing_b)};
							for(BoolExpr existing_b_or_neg : set){
							e = buildIsNotEquivalent(ctx, b, existing_b_or_neg);
							//e = ctx.mkAnd(e, buildIsNotEquivalent(ctx, ctx.mkNot(b), existing_b));
							Solver s2 = ctx.mkSolver();
							s2.add(e);
							if(s2.check() == Status.SATISFIABLE){
								continue;
							}else{
								not_implied = false;
								System.out.println("not SAT" + s2.toString());						
							}
						}}
						if(not_implied){
							ret.add(b);
						}
					}
				}	
				System.out.println(" returning:\n" + ret);

				return ret;
			}

		/**
		 * @param ctx
		 * @param b
		 * @param existing_b
		 * @return
		 */
		public static BoolExpr buildIsNotEquivalent(Context ctx, BoolExpr b, BoolExpr existing_b) {
			return ctx.mkNot(ctx.mkIff(existing_b, b));
		}
		

		/**
		 * @param ctx
		 * @param b
		 * @param existing_b
		 * @return
		 */
		public static BoolExpr buildIsNotImplied(Context ctx, BoolExpr b, BoolExpr existing_b) {
			return ctx.mkNot(ctx.mkImplies(existing_b, b));
		}
}

package fsb.extrapolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import fsb.ast.Assign;
import fsb.ast.Assume;
import fsb.ast.BoolExpr;
import fsb.ast.Statement;
import fsb.explore.Action;
import fsb.semantics.sc.SCState;
import fsb.z3.misc.FilterPredicates;

class TestFailedException extends Exception {
    private static final long serialVersionUID = 1L;

    public TestFailedException() {
        super("Check FAILED");
    }
};

public class WPInfer extends IGuessPredicates {

    Model check(Context ctx, com.microsoft.z3.BoolExpr f, Status sat) throws TestFailedException {
        Solver s = ctx.mkSolver();
        s.add(f);
        if (s.check() == Status.SATISFIABLE)
            return s.getModel();
        else
            for (Expr c : s.getUnsatCore()) {
                System.out.println(c);
            }
        return null;
    }

    @SuppressWarnings("unused")
    private int getLabel(ArrayList<Action> concrete_trace, int pid) {
        for (int i = concrete_trace.size() - 1; i >= 0; i--) {
            if (concrete_trace.get(i).getPid() == pid) {
                return concrete_trace.get(i).getStatement().getLabel();
            }
        }
        return -1;
    }
    
    public ArrayList<com.microsoft.z3.BoolExpr> assignPrecondition(Assign assign, ArrayList<com.microsoft.z3.BoolExpr> state, Context ctx) {
        
        ArrayList<com.microsoft.z3.BoolExpr> result = new ArrayList<>();
        ArithExpr freshVar = getFreshVar(assign.getZ3Dst(ctx), ctx);
        for (int j = 0; j < state.size(); j++) {
            /* For each formula, compute WP by substitution */
            result.add((com.microsoft.z3.BoolExpr) state.get(j).substitute(assign.getZ3Dst(ctx),
                    freshVar));            
        }
        result.add(ctx.mkEq(freshVar, assign.getZ3Src(ctx)));
        return result;
    }
    
    public ArrayList<com.microsoft.z3.BoolExpr> assumePrecondition(Assume assume, ArrayList<com.microsoft.z3.BoolExpr> state, Context ctx) {
        
        ArrayList<com.microsoft.z3.BoolExpr> result = new ArrayList<>();
        for (int j = 0; j < state.size(); j++) {
            result.add((com.microsoft.z3.BoolExpr) state.get(j));
        }
        result.add(assume.cond.toZ3(ctx));
        
        return result;
    }
    
    
    public void printFormulas(ArrayList<com.microsoft.z3.BoolExpr> arr) {
        System.out.println("===");
        for (int i = 0; i < arr.size(); i++) {
            System.out.println(arr.get(i));
        }
        System.out.println("===");

    }
    
    private HashMap<String, Integer> freshCounter = new HashMap<>();
    
    public ArithExpr checkFreshVar(ArithExpr var, Context ctx) {
        String varName = var.toString();
        if (freshCounter.containsKey(varName)) {
            return ctx.mkIntConst(var.toString() + freshCounter.get(varName));
        }
        
        return var;
    }
    
    public ArithExpr getFreshVar(ArithExpr var, Context ctx) {
        String varName = var.toString();
        int freshIdx;
        if (freshCounter.containsKey(varName)) {
            freshIdx = freshCounter.get(varName) + 1;
        } else {
            freshIdx = 0;
        }
        freshCounter.put(varName, freshIdx);
        
        return ctx.mkIntConst(var.toString() + freshIdx);
    }
    
    public com.microsoft.z3.BoolExpr eliminateFreshVars(com.microsoft.z3.BoolExpr expr, Context ctx) {
        for (String varName : freshCounter.keySet()) {
            ArithExpr origVar = ctx.mkIntConst(varName);
            for(int index=freshCounter.get(varName); index >= 0 ; --index){
            	ArithExpr freshVar = ctx.mkIntConst(varName + index);
            	expr = (com.microsoft.z3.BoolExpr)expr.substitute(freshVar, origVar);
            }
        }
        return expr;
    }
    
     
    //public 
    
    
    /**
     * Infers the refinement predicates using the WP (weakest precondition)
     * approach
     */
    
    @Override
    public List<Expr> guessZ3ExprPredicates(ArrayList<BoolExpr> predicates, ArrayList<Action> concrete_trace) {
        System.out.println("in wpi execute");

        /* Initialize Z3 context */
        Context ctx = initZ3();
        Expr minExpr = null;

        /* Construct Z3 formula for final state */
        /*
         * using ArrayList because the size might grow (adding conditions from
         * IFs)
         */
        SCState finalState = (SCState) concrete_trace.get(concrete_trace.size() - 1).getTarget();
        ArrayList<com.microsoft.z3.BoolExpr> final_state_formulas = extractFinalStateForZ3(predicates, finalState, ctx);

        ArrayList<com.microsoft.z3.BoolExpr> formulas = final_state_formulas;
        ArrayList<com.microsoft.z3.BoolExpr> newFormulas;
        ArrayList<com.microsoft.z3.BoolExpr> formulasLabels;
        ArrayList<Expr> result = new ArrayList<>();


        com.microsoft.z3.BoolExpr[] aux = new com.microsoft.z3.BoolExpr[1];

      //  com.microsoft.z3.BoolExpr finalStateZ3 = ctx.mkAnd(final_state_formulas.toArray(aux));
        //System.out.println("Final state: " + finalStateZ3);

        /* Compute weakest preconditions using substitution */
        newFormulas = new ArrayList<com.microsoft.z3.BoolExpr>();
        
        for (int i = concrete_trace.size() - 1; i >= 0; i--) {

            /* Iterate the concrete trace starting with the last action */
            Action action = concrete_trace.get(i);
            Statement stmt = action.getStatement();

            System.out.println(stmt + " " + stmt.getClass());

            if (stmt instanceof Assume) {
                Assume assume = (Assume) stmt;
                System.out.println("cond: " + assume.cond.toZ3(ctx));
                newFormulas = assumePrecondition(assume, formulas, ctx);
            } else if (stmt instanceof Assign) {
                Assign assign = (Assign) stmt;
                newFormulas = assignPrecondition(assign, formulas, ctx);
            }
            //printFormulas(newFormulas);
            formulasLabels = computeFormulasLabels(ctx, newFormulas);

            /* Check satisfiability of the new state, obtained by WP */
            Solver s = ctx.mkSolver();
            
            s.assertAndTrack(newFormulas.toArray(aux), formulasLabels.toArray(aux));
            
            Status z3_result = s.check();

            if (z3_result == Status.UNSATISFIABLE) {
                /* The state is UNSAT */

                System.out.println("--- UNSAT Core ---");
                minExpr = null;
                com.microsoft.z3.BoolExpr current;
                
                /*
                 * extract new predicates from UNSAT core - smallest predicate
                 */
                
                for (Expr c : s.getUnsatCore()) {
                    int formulaIdx = Integer.parseInt(c.toString().replaceAll("[^0-9]", ""));
                    
                    
                    current = newFormulas.get(formulaIdx);
                    current = eliminateFreshVars(current, ctx);
                    System.out.println(current);
                    if(!current.simplify().isFalse())
                    	result.add(current);
                    
                    if (minExpr == null) {
                        minExpr = current;
                    } else if (current.toString().length() < minExpr.toString().length()) {
                        minExpr = current;
                    }
                }
                System.out.println("min: " + minExpr);
                break;
            } else {
                /* continue the inverse iteration of the trace */
                formulas = new ArrayList<com.microsoft.z3.BoolExpr>();
                for (int j = 0; j < newFormulas.size(); j++) {
                    formulas.add(newFormulas.get(j));
                }
            }

        }
        
		
		List<com.microsoft.z3.BoolExpr> predicates_z3 = predicates.stream().map(x->x.toZ3(ctx)).collect(Collectors.toList());
		return FilterPredicates.filterPredicates(predicates_z3,  result, ctx);


    }

    /**
     * @param ctx
     * @param formulas
     * @return
     */
    private ArrayList<com.microsoft.z3.BoolExpr> computeFormulasLabels(Context ctx,
            ArrayList<com.microsoft.z3.BoolExpr> formulas) {
        ArrayList<com.microsoft.z3.BoolExpr> formulasLabels = new ArrayList<com.microsoft.z3.BoolExpr>();
        for (int i = 0; i < formulas.size(); i++) {
            formulasLabels.add(ctx.mkBoolConst("trackingLabel" + i));
        }
        return formulasLabels;
    }

    public com.microsoft.z3.BoolExpr generateZ3(BoolExpr expr, Context ctx) {

        try {
            return expr.toZ3(ctx);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    protected Context initZ3() {
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);
        return ctx;
    }
}
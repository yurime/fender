package fsb.extrapolation;

import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import fsb.ast.AritExpr;
import fsb.ast.Assign;
import fsb.ast.Assume;
import fsb.ast.Load;
import fsb.ast.Statement;
import fsb.ast.Store;
import fsb.ast.SymbolTable;
import fsb.explore.Action;
import fsb.explore.SBState;
import fsb.explore.State;
import fsb.semantics.sc.SCState;

/**
 * helper to {@link #traceToZ3Formulas}
 * @param ctx
 * @param varToInstance
 * @param action
 * @return
 */
public class ActionToZ3BoolWithRenaming { 
	
	static public  BoolExpr transform(Context ctx, ArrayList<fsb.ast.BoolExpr> predicates,
			HashMap<Integer, ArrayList<Integer>> varToInstance, Assume assume) {
		BoolExpr action_z3;
		action_z3 = assume.toZ3(ctx, predicates);
		for(int local : varToInstance.keySet()){
			ArrayList<Integer> mapping = varToInstance.get(local);
			ArithExpr from = ctx.mkIntConst(SymbolTable.getLocalVariableName(local));
			int new_local = ((mapping.size() > 0)? mapping.get(mapping.size() - 1) : 0);
			ArithExpr to = ctx.mkIntConst(SymbolTable.getLocalVariableName(new_local));
			
			action_z3 = (com.microsoft.z3.BoolExpr)action_z3.substitute(from, to);
		}
		return action_z3;
	}
	
		static public BoolExpr transform(ArrayList<String> vars, Context ctx,
				HashMap<Integer, ArrayList<Integer>> varToInstance, Store store ) {
			BoolExpr action_z3;
			 String dest_name = store.getDstName();
			 
			 ArrayList<Integer> var_instances = getOrCreateVarInstances(dest_name, vars, varToInstance);
			 
			 AritExpr src =  store.getSrc().rewrite(varToInstance);
			 
			 String new_dst_instance_name = ActionToZ3BoolWithRenaming.newVarInstance(dest_name, (var_instances.size()+1));
			 int new_dst_instance_i = SymbolTable.getOrCreateLocalVariable(new_dst_instance_name);
			 var_instances.add(new_dst_instance_i);
			 action_z3 = ctx.mkEq(ctx.mkIntConst(new_dst_instance_name),  src.toZ3(ctx));
			return action_z3;
		}
		
		static public  BoolExpr transform(ArrayList<String> vars, Context ctx,
				HashMap<Integer, ArrayList<Integer>> varToInstance, Load load) {
			 BoolExpr action_z3;
			 
			 String dest_name = load.getDstName();				 
			 ArrayList<Integer> var_instances = getOrCreateVarInstances(dest_name, vars, varToInstance);
			 
			 String src_name = load.getSrc().toString();
			 ArrayList<Integer> src_instances = getOrCreateVarInstances(src_name, vars, varToInstance);
			 
			 Integer ltst_src_id = src_instances.get(src_instances.size()-1);
			 String ltst_src_name = SymbolTable.getLocalVariableName(ltst_src_id);
			 String new_dst_instance_name = ActionToZ3BoolWithRenaming.newVarInstance(dest_name, (var_instances.size()+1));
			 int new_dst_instance_i = SymbolTable.getOrCreateLocalVariable(new_dst_instance_name);
			 var_instances.add(new_dst_instance_i);
			action_z3 = ctx.mkEq(ctx.mkIntConst(new_dst_instance_name), ctx.mkIntConst(ltst_src_name));
			return action_z3;
		}
	
		static public  BoolExpr transform(ArrayList<String> vars, Context ctx,
				HashMap<Integer, ArrayList<Integer>> varToInstance,Assign assign) {
			 
			 String dest_name = assign.getDstName();
			 ArrayList<Integer> var_instances = getOrCreateVarInstances(dest_name, vars, varToInstance);
			 
			 AritExpr src = assign.getSrc().rewrite(varToInstance);
			 
			 String new_instance_name = ActionToZ3BoolWithRenaming.newVarInstance(dest_name, (var_instances.size()));
			 int new_instance_i = SymbolTable.getOrCreateLocalVariable(new_instance_name);
			 var_instances.add(new_instance_i);
			 BoolExpr action_z3 = ctx.mkEq(ctx.mkIntConst(new_instance_name), src.toZ3(ctx));
			return action_z3;
		}
		
		static private ArrayList<Integer> getOrCreateVarInstances(String var_name, ArrayList<String> vars, HashMap<Integer, ArrayList<Integer>> varToInstance){
			 int var_id = SymbolTable.getOrCreateLocalVariable(var_name);
			 if(! vars.contains(var_name) ){
				 vars.add(var_name);
			 }
			 
			 if(! varToInstance.containsKey(var_id)){
				 varToInstance.put(var_id, new ArrayList<Integer>());
				 varToInstance.get(var_id).add(var_id);
			 }
			 return varToInstance.get(var_id);
			 
		}
		/**
		 * @param ctx
		 * @param predicates
		 * @param vars
		 * @param varToInstance
		 * @param action
		 * @return
		 */
		static public BoolExpr transform(Context ctx, ArrayList<fsb.ast.BoolExpr> predicates, ArrayList<String> vars,
				HashMap<Integer, ArrayList<Integer>> varToInstance, Statement statement) {

			switch(statement.getType()){
				case ASSIGN: return transform(vars, ctx, varToInstance, (Assign)statement);

				case ASSUME: return transform(ctx, predicates, varToInstance, (Assume)statement);
					
				case LOAD: return transform(vars, ctx, varToInstance, (Load)statement);
					
				case STORE:	return transform(vars, ctx, varToInstance, (Store)statement);

				default: throw new IllegalStateException(" a statement which is not assume/assign was sent " + statement);
			}
		}
		/**
		 * helper to {@link #traceToZ3Formulas}
		 * @param concrete_trace
		 * @param vars
		 * @param varToInstance
		 */
		@SuppressWarnings("unused")
		static public void addPcAsVars(ArrayList<Action> concrete_trace, ArrayList<String> vars,
				HashMap<Integer, ArrayList<Integer>> varToInstance) {
			for(int i=0; i < concrete_trace.get(0).getTarget().getProcs(); ++ i){
				String pc = ActionToZ3BoolWithRenaming.newVarInstance("pc", i);
				 int new_instance_i = SymbolTable.getOrCreateLocalVariable(pc);
				 varToInstance.put(new_instance_i, new ArrayList<Integer>());
				 varToInstance.get(new_instance_i).add(new_instance_i);
				 vars.add(pc);
				
			}
		}
		/**
		 * helper to {@link #traceToZ3Formulas}
		 * @param ctx
		 * @param varToInstance
		 * @param action
		 * @return
		 */
		@SuppressWarnings("unused")
		public static  ArrayList<BoolExpr> collectPCsFromAction(Context ctx, HashMap<Integer, ArrayList<Integer>> varToInstance,
				Action action) {
			SCState state = (SCState)action.getTarget();
			 ArrayList<BoolExpr> pc_s = new ArrayList<>(); 
			 int n = state.getProcs();
			 for (int i = 0; i < n; i++) {
					String pc = "pc_" + i;
				 	int old_pc_i = SymbolTable.getOrCreateLocalVariable(pc);
				    int currRealPC = ((SBState) state).getPC(i);
				    
					ArrayList<Integer> var_instances = varToInstance.get(old_pc_i);
					String new_pc_name = ActionToZ3BoolWithRenaming.newVarInstance(pc, (var_instances.size()+1));
					int new_pc_i = SymbolTable.getOrCreateLocalVariable(new_pc_name);
					
					var_instances.add(new_pc_i);
				    int label = State.program.getListing(i).get(currRealPC).getLabel();
				    pc_s.add(ctx.mkEq(ctx.mkIntConst(new_pc_name), ctx.mkInt(label)));
			}
			return pc_s;
		}

		public static String newVarInstance(String dest_name, int instance_num) {
			return dest_name + ActionToZ3BoolWithRenaming.N_I_SEP + instance_num;
		}

		public static final String N_I_SEP ="@";

}// end of helper class ActionToZ3BoolWithRenaming
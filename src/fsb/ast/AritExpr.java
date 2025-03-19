package fsb.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.State;
import fsb.tvl.ArithValue;

public interface AritExpr {
	public enum AXType {PLUS, MINUS, MUL};
	public ArithValue evaluate(State s, int pid);
	public List<Integer> getVariablesInvolved() ;
	AritExpr rewrite(Map<Integer, ArrayList<Integer>> change);
	public ArithExpr toZ3(Context ctx);
}

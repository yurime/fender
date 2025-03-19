package fsb.ast;

import java.util.Set;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.State;
import fsb.tvl.ArithValue;

public interface AssertAritExpr {
	public ArithValue evaluate(State s);
	public Set<Integer> getVariablesInvolved() ;
	public AssertAritExpr assignConcreteValuesFromState(State s);
	public ArithExpr toZ3(Context ctx);
}

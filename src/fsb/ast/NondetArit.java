package fsb.ast;

import java.util.List;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import java.util.ArrayList;
import java.util.Collections;

import fsb.explore.State;
import fsb.tvl.ArithValue;
import fsb.tvl.NondetArithValue;

//Like NondetBool, this is a huge hack.
//In this case, though, it's not clear how it could be avoided.
public class NondetArit implements AritExpr {
	int val;
	
	public NondetArit() { }

	@Override
	public ArithValue evaluate(State s, int pid) {
		//throw new RuntimeException("Should not try to evaluate non-deterministic expressions");
		return NondetArithValue.getInstance();
	}
	
	public String toString()
	{
		return "*";
	}
	
	@Override
	public List<Integer> getVariablesInvolved() {
		return Collections.emptyList();
	}
	
	@Override
	public AritExpr rewrite(Map<Integer, ArrayList<Integer>> change) {
			return new NondetArit();
		}

	@Override
	public ArithExpr toZ3(Context ctx){
		throw new IllegalStateException("Non-determined Arithmetic Expcession encountered");
	}
	
}

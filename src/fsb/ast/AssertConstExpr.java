package fsb.ast;

import java.util.Collections;
import java.util.Set;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.State;
import fsb.tvl.ArithValue;
import fsb.tvl.DetArithValue;

public class AssertConstExpr implements AssertAritExpr {
	final ArithValue val;

	public AssertConstExpr(int val) {
		this.val = DetArithValue.getInstance(val);
	}

	@Override
	public ArithValue evaluate(State s) {
		return val;
	}

	public String toString() {
		if(getIntVal() < 0){
				return "(0 - " + (-getIntVal()) + ")";
		}
		return "" + val;
	}

	@Override
	public Set<Integer> getVariablesInvolved() {
		return Collections.emptySet();
	}

	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof AssertConstExpr) ){
			return false;
		}
		AssertConstExpr other =(AssertConstExpr) obj;
		
		return other.val == val;
	}
	
	@Override
	public int hashCode() {
		return val.hashCode();
	}

	@Override
	public AssertAritExpr assignConcreteValuesFromState(State s) {
		return this;
	}


    @Override
    public ArithExpr toZ3(Context ctx) {
        if (val.isDetermined()) {
            return ctx.mkInt(getIntVal());
        } else {
            throw new IllegalStateException("Non-determined Arithmetic value encountered");
        }
    }
    

	public int getIntVal() {
		if (val.isDetermined()) {
			return ((DetArithValue)val).getValue();
		} else {
			throw new IllegalStateException("Non-determined Arithmetic value encountered");
		}
	}
}

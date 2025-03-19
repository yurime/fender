package fsb.ast;

import java.util.List;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import java.util.ArrayList;
import java.util.Collections;

import fsb.explore.State;
import fsb.tvl.ArithValue;
import fsb.tvl.DetArithValue;

public class ConstExpr implements AritExpr {
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstExpr other = (ConstExpr) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	ArithValue val;
	
	public ConstExpr(int val)
	{
		this.val = DetArithValue.getInstance(val);	
	}

	public ConstExpr(ArithValue val) {
		this.val = val;
	}

	@Override
	public ArithValue evaluate(State s, int pid) {
		return val;
	}
	
	public String toString()
	{
		if(getIntVal() < 0){
			return "(0 - " + (-getIntVal()) + ")";
		}
		return "" + val;
	}
	
	@Override
	public List<Integer> getVariablesInvolved() {
		return Collections.emptyList();
	}
	
	@Override
	public AritExpr rewrite(Map<Integer, ArrayList<Integer>> change) {
		return new ConstExpr(val);
	}

	@Override
	public ArithExpr toZ3(Context ctx) {
			return ctx.mkInt(getIntVal());
	}


	public int getIntVal() {
		if (val.isDetermined()) {
			return ((DetArithValue)val).getValue();
		} else {
			throw new IllegalStateException("Non-determined Arithmetic value encountered");
		}
	}
}

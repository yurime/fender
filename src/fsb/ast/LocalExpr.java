package fsb.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.State;
import fsb.tvl.ArithValue;

public class LocalExpr implements AritExpr {
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + local;
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
		LocalExpr other = (LocalExpr) obj;
		if (local != other.local)
			return false;
		return true;
	}

	int local;
	protected ArrayList<Integer> m_involvingVariables = null;

	public LocalExpr(String local) {
		this.local = SymbolTable.getOrCreateLocalVariable(local);
	}
	
	public LocalExpr(int local) {
		this.local = local;
	}

	@Override
	public ArithValue evaluate(State s, int pid) {
		return s.getLocal(pid, local);
	}

	public String toString() {
		return SymbolTable.getLocalVariableName(local);
	}

	@Override
	public List<Integer> getVariablesInvolved() {
		if (null == m_involvingVariables) {
			m_involvingVariables = new ArrayList<Integer>();
			m_involvingVariables.add(local);
		}
		return m_involvingVariables;
	}
	
	@Override
	public AritExpr rewrite(Map<Integer, ArrayList<Integer>> change) {
		ArrayList<Integer> mapping = change.get(local);
		int new_local = ((mapping != null && mapping.size() > 0)? mapping.get(mapping.size() - 1) : local);		
		return new LocalExpr(new_local);
	}

	@Override
	public ArithExpr toZ3(Context ctx) {
		return ctx.mkIntConst(SymbolTable.getLocalVariableName(local));
	}
	
}

package fsb.ast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.microsoft.z3.Context;

import fsb.ast.BoolExpr.BXType;
import fsb.explore.State;
import fsb.tvl.ArithValue;
import fsb.tvl.BoolValue;

public class AssertComparsionBool extends AssertBoolExpr {	
	private AssertAritExpr left, right;
	private BoolExpr.BXType type;
	private Set<Integer>  m_involvingVariables = null;
	
	public AssertComparsionBool(AssertAritExpr left, AssertAritExpr right, BoolExpr.BXType type)
	{
		this.left = left;
		this.right = right;
		this.type = type;
	}
	
	public BoolValue evaluate_partVisb(State s)
	{
		ArithValue leftval = left.evaluate(s);
		ArithValue rightval = right.evaluate(s);
		
		return leftval.switchCmpr(rightval,type);
	}
	
	public String toString()
	{
		String typeStr;
		switch (type)
		{
			case EQUAL:
				typeStr = "==";
				break;
			case NEQ:
				typeStr = "!=";
				break;
			case GREATER:
				typeStr = ">";
				break;
			case LESS:
				typeStr = "<";
				break;
			default:
				throw new RuntimeException("Invalid type for boolean expression!");
		}	
		
		return "(" + left.toString() + " " + typeStr + " " + right.toString() + ")";
	}
	@Override
	public Set<Integer> getVariablesInvolved() {
		if(null == m_involvingVariables){
			m_involvingVariables = left.getVariablesInvolved();
			m_involvingVariables.addAll(right.getVariablesInvolved());
		}
		return m_involvingVariables;
	}
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof AssertComparsionBool) ){
			return false;
		}

		AssertComparsionBool other = (AssertComparsionBool)obj;
		
		if(! other.left.equals(left))
		return false;
		
		if(! other.right.equals(right))
			return false;
		
		if(! other.type.equals(type))
			return false;
		
		return true;
	};
	@Override
	public int hashCode() {
		int ret = left.hashCode();
		
		ret = ret*17 + right.hashCode();
		
		ret = ret*17 + type.hashCode();
		
		return ret;
	}

	@Override
	public AssertBoolExpr assignConcreteValuesFromState(State s) {
		AssertAritExpr newLeft = left.assignConcreteValuesFromState(s);
		AssertAritExpr newRight = right.assignConcreteValuesFromState(s);
		
		if(newLeft instanceof AssertConstExpr && newRight instanceof AssertConstExpr){
			AssertConstExpr constValLeft = (AssertConstExpr)newLeft;
			AssertConstExpr constValRight = (AssertConstExpr)newRight;
			AssertBoolExpr b = new AssertConstBool(constValLeft.val.switchCmpr(constValRight.val, type));
			return returnGivenAssertBoolExprOrHashed(b);
		}
		AssertBoolExpr b = new AssertComparsionBool(newLeft, newRight, type);
		return returnGivenAssertBoolExprOrHashed(b);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<AssertBoolExpr> allSatisfiedCubes(State s){
		
		if(evaluate_partVisb(s).isTrue()){
			HashSet<AssertBoolExpr> ret = new HashSet<AssertBoolExpr>();
			ret.add(this);
			return ret;
		}
		return Collections.EMPTY_SET;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Set<AssertBoolExpr> allNegationSatisfiedCubes(State s){
		if(evaluate_partVisb(s).isFalse()){
			HashSet<AssertBoolExpr> ret = new HashSet<AssertBoolExpr>();
			ret.add(this);
			return ret;
		}
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<AssertBoolExpr> allCubes() {
		HashSet<AssertBoolExpr> ret = new HashSet<AssertBoolExpr>();
		ret.add(this);
		return ret;
	}
	
	@Override
	public boolean isNegatedDNF() {
		
		return false;
	}

    @Override
    public com.microsoft.z3.BoolExpr toZ3(Context ctx){
      
        // TODO: generalize test if left contains B
        if (left instanceof AssertGlobal && right instanceof AssertConstExpr && left.toString().contains("B")) {
            // of type B3 = 0, ...
            return globalConstComparisonToZ3(ctx);
        } 
        return switchTypeToZ3(ctx);   
    }

	private com.microsoft.z3.BoolExpr globalConstComparisonToZ3(Context ctx) {
		/* We identify expressions of the type (B0 == 0) */
		int val = Integer.parseInt(((AssertConstExpr)right).toString());
		int predIdx = Integer.parseInt(((AssertGlobal)left).toString().replaceAll("[^0-9]", ""));
		/* And replace the global variable B0 with the corresponding predicate */
		if ((val == 1 && type == BXType.EQUAL) || (val == 0 && type == BXType.NEQ)) {
		    return getPredicates().get(predIdx).toZ3(ctx);
		}
		if ((val == 0 && type == BXType.EQUAL) || (val == 1 && type == BXType.NEQ)) {
		    return ctx.mkNot(getPredicates().get(predIdx).toZ3(ctx));
		}
		throw new RuntimeException("Invalid State for AssertComparison of Global and Const! " + this.toString());
	}
	
	private com.microsoft.z3.BoolExpr switchTypeToZ3(Context ctx)  {
		switch (type)
        {
            case EQUAL:
            	return ctx.mkEq(left.toZ3(ctx), right.toZ3(ctx));
            case NEQ:
                return ctx.mkNot(ctx.mkEq(left.toZ3(ctx), right.toZ3(ctx)));
            case GREATER:
                return ctx.mkGt(left.toZ3(ctx), right.toZ3(ctx));
            case LESS:
                return ctx.mkLt(left.toZ3(ctx), right.toZ3(ctx));
            default:
                throw new RuntimeException("Invalid type for Assert Comparison boolean expression! " +  this.toString());
        }
	}


}

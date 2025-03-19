package fsb.explore;

import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import fsb.ast.AssertBoolExpr;

public class ExprValidator extends Validator {
	private AssertBoolExpr assertion;
	private boolean alwaysActive;
	
	public ExprValidator(AssertBoolExpr b, boolean alwaysActive)
	{
		assertion = b;
		this.alwaysActive = alwaysActive;
	}
	@Override
	
	public boolean isErr(State s) {
		// YURI: added that assert always is not evaluated during initialization
		// if the assertion evaluates to unknown considering it a breach.
		return (s.isFinal() || (alwaysActive && s.isInitFinished() && (-1==s.inAtomicSection()))) && !(assertion.evaluate(s).isTrue()) ;
	}
	
	public String getAssertion() {
		return assertion.toString();
	}
	
	public BoolExpr toZ3(Context ctx, ArrayList<fsb.ast.BoolExpr> predicates) {
	    AssertBoolExpr.setPredicates(predicates);
	    return assertion.toZ3(ctx);
	}

}

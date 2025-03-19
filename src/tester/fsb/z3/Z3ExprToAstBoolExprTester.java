/**
 * 
 */
package tester.fsb.z3;

import static org.junit.Assert.*;

import org.junit.Test;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import fsb.z3.Z3ExprToAstBoolExpr;
import fsb.z3.Z3Printer;

/**
 * @author user
 *
 */

	
public class Z3ExprToAstBoolExprTester {





	@Test
	public void orTransformationNormal(){
		// following a specific case:
		//(or (= f2 0) (not (= t2 0)))
		
		Context c = new Context();
		Expr f2 = c.mkIntConst("f2");
		Expr t2 = c.mkIntConst("t2");
		Expr zero = c.mkInt(0);
		Expr e = c.mkOr(c.mkEq(f2, zero),  c.mkNot(c.mkEq(t2, zero)));
		String res = Z3ExprToAstBoolExpr.toBoolExpr(e).toString();
		assertEquals("((f2 = 0) or (t2 != 0))", res);				
	}

	@Test
	public void andTransformationNormal(){
		//(or (= f2 0) (not (= t2 0)))
		
		Context c = new Context();
		Expr f2 = c.mkIntConst("f2");
		Expr t2 = c.mkIntConst("t2");
		Expr zero = c.mkInt(0);
		Expr e = c.mkAnd(c.mkEq(f2, zero),  c.mkNot(c.mkEq(t2, zero)));
		String res = Z3ExprToAstBoolExpr.toBoolExpr(e).toString();
		assertEquals("((f2 = 0) and (t2 != 0))", res);				
	}
		
	@Test
	public void f2eqZeroTransformationNormal(){
		
		Context c = new Context();
		Expr f2 = c.mkIntConst("f2");
		Expr zero = c.mkInt(0);
		
		Expr e = c.mkEq(f2, zero);
		String res = Z3ExprToAstBoolExpr.toBoolExpr(e).toString();
		assertEquals("(f2 = 0)", res);
		
	}
	
	
	@Test
	public void f2eqt2TransformationNormal(){
		
		Context c = new Context();
		Expr f2 = c.mkIntConst("f2");
		Expr t2 = c.mkIntConst("t2");
		
		Expr e = c.mkEq(f2, t2);
		String res = Z3ExprToAstBoolExpr.toBoolExpr(e).toString();
		assertEquals("(f2 = t2)", res);
		
	}

	@Test
	public void zeroEqt2TransformationNormal(){
		
		Context c = new Context();
		Expr zero = c.mkInt(0);
		Expr t2 = c.mkIntConst("t2");
		
		Expr e = c.mkEq(zero, t2);
		String res = Z3ExprToAstBoolExpr.toBoolExpr(e).toString();
		assertEquals("(t2 = 0)", res);
		
	}

	@Test
	public void notComplexExprTransformationNormal(){
		// following a specific case:
	//!(((f2 = 0)   or   (t2 != 0)))
	//	(not (or (= f2 0) (not (= t2 0))))
		Context c = new Context();
		Expr f2 = c.mkIntConst("f2");
		Expr t2 = c.mkIntConst("t2");
		Expr zero = c.mkInt(0);

		Expr e = c.mkNot(c.mkOr(c.mkEq(zero, f2), c.mkNot(c.mkEq(t2, zero))));
		String res = Z3ExprToAstBoolExpr.toBoolExpr(e).toString();
		assertEquals("((f2 != 0) and (t2 = 0))", res);
	}
}

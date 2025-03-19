package fsb.z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;

import fsb.ast.AritExpr;
import fsb.ast.AritExpr.AXType;


/**
 * A class for converting z3 boolean expression to fsb.ast.BoolExpr.<br> 
 * usage: <br>
 * to simplify an expr:<br>
 * Z3Printer.toString(expr)<br><br>
 * <br>
 * @author unknown(Kasper Føns and Jacob Hougaard?)<br>
 * 
 */
public class Z3ExprToAstBoolExpr {


	    public static fsb.ast.BoolExpr toBoolExpr(Expr expr) {
	        Z3ExprToAstBoolExpr z3Printer = new Z3ExprToAstBoolExpr();
	        if(expr.isBool()) {
		           return z3Printer.convertBoolExpr((BoolExpr) expr);
		        }
	        throw new RuntimeException(expr.toString() + "");
	    }

	    private fsb.ast.BoolExpr convertBoolExpr(Expr expr) {
	        if(expr.isBool()) {
	           return convertBoolExpr((BoolExpr) expr);
	        } else {
		        throw new RuntimeException("expected boolean expression and got: " + expr.toString());
	        }
	    }
	    private fsb.ast.BoolExpr convertNotBoolExpr(Expr expr) {
	        if(expr.isBool()) {
	           return convertNotBoolExpr((BoolExpr) expr);
	        } else {
		        throw new RuntimeException("expected boolean expression and got: " + expr.toString());
	        }
	    }
	    private fsb.ast.AritExpr convertAritExpr(Expr expr) {
	    	if(expr.isIntNum()) {
	        	 com.microsoft.z3.IntNum num = (com.microsoft.z3.IntNum)expr;
	        	 return new fsb.ast.ConstExpr(num.getInt());
	        }
	    	if(expr.isConst()) {
	        	 return new fsb.ast.LocalExpr(expr.toString());
	        }
	    	if(expr.isApp()) {
	    		AXType type;
	        	if(expr.isAdd()){
	        		type = AXType.PLUS;
		        }else if(expr.isSub()) {
	        		type = AXType.MINUS;
		        }else if(expr.isMul()) {
	        		type = AXType.MUL;
		        }else {
		            throw new RuntimeException("unexpected type in: " + expr.toString());
		        }
	        	Expr[] args = expr.getArgs();
        		AritExpr running_expr = convertAritExpr(args[0]);
	        	for (int i_arg=1; i_arg < args.length; ++i_arg ){
	        		AritExpr right = convertAritExpr(args[i_arg]);
	        		running_expr = new fsb.ast.ComplexAritExpr(running_expr, right, type);
	        	}
	        	return running_expr;
	        	
	        } else {
	            throw new RuntimeException("cannont convert " + expr.toString());
	        }
	    }
	 

	    private fsb.ast.BoolExpr convertBoolExpr(BoolExpr boolExpr) {
	        Expr[] args = boolExpr.getArgs();
	        if (boolExpr.isFalse()) {
	            return new fsb.ast.ConstBool(false);
	        } else if (boolExpr.isTrue()) {
	        	return new fsb.ast.ConstBool(true);
	        } else if (boolExpr.isAnd()) {
	            return convertExprArray(fsb.ast.BoolExpr.BXType.AND, args);
	        } else if (boolExpr.isOr()) {
	        	   return convertExprArray(fsb.ast.BoolExpr.BXType.OR, args);
	        } else if (boolExpr.isIff()) {
	        	   return convertExprArray(fsb.ast.BoolExpr.BXType.IFF, args);
            } else if (boolExpr.isNot()) {
            	assertArgsLength(args, 1);
	            return convertNotBoolExpr(args[0]);
	        } else {
	            return convertComparisonExpr(boolExpr, args);
	        }
	    }

		/**
		 * @param boolExpr
		 * @param args
		 * @return
		 */
		private fsb.ast.BoolExpr convertComparisonExpr(BoolExpr boolExpr, Expr[] args) {
			assertArgsLength(args, 2);
			Expr first = args[0];
			Expr second = args[1];
			if (boolExpr.isEq()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.EQUAL);
			} else if (boolExpr.isGE()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.GE);
			} else if (boolExpr.isLE()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.LE);
			} else if (boolExpr.isGT()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.GREATER);
			} else if (boolExpr.isLT()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.LESS);
			} else {
			    throw new RuntimeException("expression of unexpected type " + boolExpr.toString());
			}
		}
		/**
		 * @param boolExpr
		 * @param args
		 * @return
		 */
		private fsb.ast.BoolExpr convertNotComparisonExpr(BoolExpr boolExpr, Expr[] args) {
			assertArgsLength(args, 2);
			Expr first = args[0];
			Expr second = args[1];
			if (boolExpr.isEq()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.NEQ);
			} else if (boolExpr.isGE()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.LESS);
			} else if (boolExpr.isLE()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.GREATER);
			} else if (boolExpr.isGT()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.LE);
			} else if (boolExpr.isLT()) {
				return outputComparison(first, second, fsb.ast.BoolExpr.BXType.GE);
			} else {
			    throw new RuntimeException("expression of unexpected type " + boolExpr.toString());
			}
		}

	    private fsb.ast.BoolExpr convertNotBoolExpr(BoolExpr boolExpr) {
	        Expr[] args = boolExpr.getArgs();
	        if (boolExpr.isFalse()) {
	            return new fsb.ast.ConstBool(true);
	        } else if (boolExpr.isTrue()) {
	        	return new fsb.ast.ConstBool(false);
	        } else if (boolExpr.isAnd()) {
	            return convertNotExprArray(fsb.ast.BoolExpr.BXType.OR, args);
	        } else if (boolExpr.isOr()) {
	        	   return convertNotExprArray(fsb.ast.BoolExpr.BXType.AND, args);
	        } else if (boolExpr.isIff()) {
	        	   return convertNotExprArray(fsb.ast.BoolExpr.BXType.IFF, args);
            } else if (boolExpr.isNot()) {
            	assertArgsLength(args, 1);
	            return convertBoolExpr(args[0]);
	        } else {
	            return convertNotComparisonExpr(boolExpr, args);
	        }
	    }


	    private fsb.ast.BoolExpr outputComparison(Expr first, Expr second,  fsb.ast.BoolExpr.BXType type ) {
	        if (!first.isConst() && second.isConst()) {
	        	return outputComparison(second, first, type);
	        } 
            fsb.ast.AritExpr left = convertAritExpr(first);
            fsb.ast.AritExpr right = convertAritExpr(second);
            return new fsb.ast.ComparsionBool(left, right, type);
    }
        



	    private fsb.ast.BoolExpr convertExprArray(fsb.ast.BoolExpr.BXType functionName, Expr[] args) {
	        fsb.ast.BoolExpr expr_so_far = convertBoolExpr(args[0]);
	        for (int i_arg=1; i_arg < args.length; ++i_arg) {
	        	Expr arg = args[i_arg];
	        	expr_so_far = new fsb.ast.ComplexBool(expr_so_far, convertBoolExpr(arg), functionName);
	        }
	        return expr_so_far;
	    }
	    
	    private fsb.ast.BoolExpr convertNotExprArray(fsb.ast.BoolExpr.BXType functionName, Expr[] args) {
	        fsb.ast.BoolExpr expr_so_far = convertNotBoolExpr(args[0]);
	        for (int i_arg=1; i_arg < args.length; ++i_arg) {
	        	Expr arg = args[i_arg];
	        	expr_so_far = new fsb.ast.ComplexBool(expr_so_far, convertNotBoolExpr(arg), functionName);
	        }
	        return expr_so_far;
	    }

	    private void assertArgsLength(Expr[] args, int assertedLength) {
	        if (args.length != assertedLength)
	            throw new RuntimeException("Argument list was not size " + assertedLength + ", it was " + args.length);
	    }

	
}

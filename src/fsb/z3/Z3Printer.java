package fsb.z3;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BitVecNum;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;

/**
 * A class for doing z3 prefix style to a string of infix style formula.<br> 
 * This class also does some basic formula simplifications, such as for negation of comparison expressions. <br>
 * <br>
 * usage: <br>
 * to simplify an expr:<br>
 * Z3Printer.toString(expr)<br><br>
 * Yuri: added support for IFF
 * <br>
 * This is a class taken from the DASH project at:<br>
 * https://github.com/foens/dash/blob/master/program/src/main/java/dk/au/cs/dash/util/Z3Printer.java
 * @author unknown(Kasper Føns and Jacob Hougaard?)<br>
 * 
 *
 */
public class Z3Printer {

	    private StringBuilder out = new StringBuilder();
	    private StringBuilder indention = new StringBuilder();
	    private static final String ONE_INDENTATION = " ";


	    public static String toString(Expr expr) {
	        Z3Printer z3Printer = new Z3Printer();
	        z3Printer.convertExpr(expr);
	        String res = z3Printer.out.toString();
	        if(res.contains("false")){
	        	System.out.println("Z3Printer warning: " + expr + "turned to " + res);
	        }
	        return res;
	    }

	    private void convertExpr(Expr expr) {
	        if(expr.isBool()) {
	            convertBoolExpr((BoolExpr) expr);
	        } else if(expr.isBV()) {
	            convertBitVecExpr((BitVecExpr) expr);
	        }else if(expr.isConst()) {
	        	 out.append(expr.toString());
	        }else if(expr.isIntNum()) {
	        	String s = expr.toString();
	        	try{
	        	int int_val = Integer.parseInt(s);
	        	if(int_val < 0){
	        		out.append("(0 - " + (-int_val) + ')');
	        	}else{
	        	 out.append(s);
	        	} 
	        	}catch (NumberFormatException e) {
	        		throw new RuntimeException(expr.toString() + " was attempted to be parsed as int and failed");
				}
	        }else if(expr.isApp()) {

	            convertArithApplication(expr);
	        	
	        } else {
	            throw new RuntimeException(expr.toString());
	        }
	    }

		/**
		 * @param expr
		 */
		private void convertArithApplication(Expr expr) {
			Expr[] args = expr.getArgs();
			String symbol = null;
			if (!args[0].isConst() && args[1].isConst()) {
	            Expr tmp = args[0];
	            args[0] = args[1];
	            args[1] = tmp;
	        }
			if(expr.isAdd()){
				symbol = " + ";
			}else if(expr.isSub()) {
				symbol = " - ";
			}else if(expr.isMul()) {
				symbol = " * ";
			}else if(expr.isDiv()) {
				symbol = " / ";
			}else {
			    throw new RuntimeException(expr.toString());
			}
			out.append("(");
			convertExpr(args[0]);
			for (int i_arg=1; i_arg < args.length; ++i_arg ){
				out.append(symbol);
				convertExpr(args[i_arg]);
			}
			out.append(")");
		}

	    private void convertBitVecExpr(BitVecExpr bitVecExpr) {
	        if(bitVecExpr.isBVNumeral()) {
	            out.append((int)((BitVecNum)bitVecExpr).getLong());
	        } else if(bitVecExpr.isConst()) {
	            out.append(bitVecExpr.toString().replaceAll("\\|", ""));
	        } else if(bitVecExpr.isBVUMinus()) {    // bvneg   -<something>
	            Expr[] args = bitVecExpr.getArgs();
	            out.append("-");
	            convertExpr(args[0]);
	        } else {
	            String symbol = null;

	            if(bitVecExpr.isBVAdd()) {
	                symbol = " + ";
	            } else if(bitVecExpr.isBVSub()) {
	                symbol = " - ";
	            } else if(bitVecExpr.isBVMul()) {
	                symbol = " * ";
	            } else if(bitVecExpr.isBVSDiv()) {
	                symbol = " / ";
	            } else if(bitVecExpr.isBVSRem() || bitVecExpr.getFuncDecl().getName().toString().equals("bvsrem_i")) {
	                symbol = " % ";
	            } else if(bitVecExpr.isBVAND()) {
	                symbol = " & ";
	            } else if(bitVecExpr.isBVOR()) {
	                symbol = " | ";
	            } else if(bitVecExpr.isBVXOR()) {
	                symbol = " ^ ";
	            } else if(bitVecExpr.isBVShiftLeft()) {
	                symbol = " << ";
	            } else if(bitVecExpr.isBVShiftRightArithmetic()) {
	                symbol = " >> ";
	            } else if(bitVecExpr.isBVShiftRightLogical()) {
	                symbol = " >>> ";
	            }


	            if (symbol != null) {
	                Expr[] args = bitVecExpr.getArgs();
	                out.append("(");
	                convertExpr(args[0]);
	                out.append(symbol);
	                convertExpr(args[1]);
	                out.append(")");
	            } else {
	                out.append("FAIL=" + bitVecExpr.toString());
	            }
	        }
	    }

	    private void convertBoolExpr(BoolExpr boolExpr) {
	        Expr[] args = boolExpr.getArgs();
	        if (boolExpr.isFalse()) {
	            out.append("false");
	        } else if (boolExpr.isTrue()) {
	            out.append("true");
	        } else if (boolExpr.isAnd()) {
	            convertExprArray("&&", args);
	        } else if (boolExpr.isOr()) {
	            convertExprArray("||", args);
	        } else if (boolExpr.isIff()) {
            	convertExprArray("=", args);
            } else if (boolExpr.isNot()) {
            	assertArgsLength(args, 1);
	            converExprNot(args[0]);
	        } else {
	            assertArgsLength(args, 2);
	            Expr first = args[0];
	            Expr second = args[1];
	            if (boolExpr.isEq()) {
	            	outputEqual(first, second);
	            }else if (boolExpr.isBVSLE()) {
	                outputLessThanEqual(first, second);
	            } else if (boolExpr.isBVSLT()) {
	                outputLessThan(first, second);
	            } else if (boolExpr.isBVSGE()) {
	                outputGreaterThanEqual(first, second);
	            } else if (boolExpr.isBVSGT()) {
	                outputGreaterThan(first, second);
	            } else if (boolExpr.isGE()) {
	            	outputGreaterThanEqual(first, second);
	            } else if (boolExpr.isLE()) {
	            	outputLessThanEqual(first, second);
	            } else if (boolExpr.isGT()) {
	            	outputGreaterThan(first, second);
	            } else if (boolExpr.isLT()) {
	            	outputLessThan(first, second);
	            } else if (boolExpr.isEq()) {
	            	outputEqual(first, second);
	            } else {
	                throw new RuntimeException(boolExpr.toString());
	            }
	        }
	    }

		private void converExprNot(Expr internalExpr) {
			if(!internalExpr.isBool())
			    throw new RuntimeException(internalExpr.toString());
			Expr[] internalArgs = internalExpr.getArgs();
			if (internalExpr.isEq()) {
				assertArgsLength(internalArgs, 2);
			    outputNotEqual(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isBVSLE()) { // !(a≤b)  ==>  a>b
			    assertArgsLength(internalArgs, 2);
			    outputGreaterThan(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isBVSLT()) { // !(a<b)  ==>  a≥b
			    assertArgsLength(internalArgs, 2);
			    outputGreaterThanEqual(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isBVSGT()) { // !(a>b)  ==>  a≤b
			    assertArgsLength(internalArgs, 2);
			    outputLessThanEqual(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isBVSGE()) { // !(a≥b)  ==>  a<b
			    assertArgsLength(internalArgs, 2);
			    outputLessThan(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isLE()) { // !(a≤b)  ==>  a>b
			    assertArgsLength(internalArgs, 2);
			    outputGreaterThan(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isLT()) { // !(a<b)  ==>  a≥b
			    assertArgsLength(internalArgs, 2);
			    outputGreaterThanEqual(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isGT()) { // !(a>b)  ==>  a≤b
			    assertArgsLength(internalArgs, 2);
			    outputLessThanEqual(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isGE()) { // !(a≥b)  ==>  a<b
			    assertArgsLength(internalArgs, 2);
			    outputLessThan(internalArgs[0], internalArgs[1]);
			} else if (internalExpr.isOr()) { // !(a≥b)  ==>  a<b			    
			    convertExprArrayNegate("&&", internalArgs);
			} else if (internalExpr.isAnd()) { // !(a≥b)  ==>  a<b
			    assertArgsLength(internalArgs, 2);
			    convertExprArrayNegate("||", internalArgs);
			} else if (internalExpr.isNot()) { // !(a≥b)  ==>  a<b
			    assertArgsLength(internalArgs, 1);
			    convertExpr(internalArgs[0]);
			} else {
			    out.append("!(");
			    convertExpr(internalExpr);
			    out.append(")");
			}
		}

	    private void assertArgsLength(Expr[] args, int assertedLength) {
	        if (args.length != assertedLength)
	            throw new RuntimeException("Argument list was not size " + assertedLength + ", it was " + args.length);
	    }

	    
	    private void outputGreaterThan(Expr first, Expr second) {
	        if (!first.isConst() && second.isConst()) {
	            outputLessThan(second, first);
	        } else {
	            out.append("(");
	            convertExpr(first);
	            out.append(" > ");
	            convertExpr(second);
	            out.append(")");
	        }
	    }

	    private void outputGreaterThanEqual(Expr first, Expr second) {
	        if (!first.isConst() && second.isConst()) {
	            outputLessThanEqual(second, first);
	        } else {
	            out.append("(");
	            convertExpr(first);
	            out.append(" >= ");
	            convertExpr(second);
	            out.append(")");
	        }

	    }

	    private void outputLessThan(Expr first, Expr second) {
	        if (!first.isConst() && second.isConst()) {
	            outputGreaterThan(second, first);
	        } else {
	            out.append("(");
	            convertExpr(first);
	            out.append(" < ");
	            convertExpr(second);
	            out.append(")");
	        }
	    }

	    private void outputEqual(Expr first, Expr second) {
		        if (!first.isConst() && second.isConst()) {
		        	outputEqual(second, first);
		        	return;
		        } 
		        out.append("(");
	            convertExpr(first);
	            out.append(" = ");
	            convertExpr(second);
	            out.append(")");
	    }
	    
	    //outputNotEqual
	    private void outputNotEqual(Expr first, Expr second) {
	        if (!first.isConst() && second.isConst()) {
	        	outputNotEqual(second, first);
	        	return;
	        } 
	        out.append("(");
            convertExpr(first);
            out.append(" != ");
            convertExpr(second);
            out.append(")");
	    }
	    
	    private void outputLessThanEqual(Expr first, Expr second) {
	        if (!first.isConst() && second.isConst()) {
	            outputGreaterThanEqual(second, first);
	        } else {
	            out.append("(");
	            convertExpr(first);
	            out.append(" <= ");
	            convertExpr(second);
	            out.append(")");
	        }
	    }

	    private void convertExprArray(String functionName, Expr[] args) {
	        out.append('(');
	        indention.append(ONE_INDENTATION);
	        convertExpr(args[0]);
	        for (int i_arg=1; i_arg < args.length; ++i_arg) {
	        	Expr arg = args[i_arg];
	            out.append(indention);
		        out.append(functionName);
	            out.append(indention);
	            convertExpr(arg);
	        }
	        indention.setLength(indention.length() - ONE_INDENTATION.length());
	        out.append(')');
	    }

	    private void convertExprArrayNegate(String functionName, Expr[] args) {
	    	out.append('(');
	        indention.append(ONE_INDENTATION);
			if(!args[0].isBool())
			    throw new RuntimeException(args[0].toString());
			converExprNot(args[0]);
	        for (int i_arg=1; i_arg < args.length; ++i_arg) {
	        	Expr arg = args[i_arg];
	            out.append(indention);
		        out.append(functionName);
	            out.append(indention);
				if(!arg.isBool())
				    throw new RuntimeException(arg.toString());
				converExprNot(arg);
	        }
	        indention.setLength(indention.length() - ONE_INDENTATION.length());
	        out.append(')');
	    }
	
}

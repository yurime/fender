package fsb.z3;

import java.util.HashMap;

import com.microsoft.z3.*;

public class JavaZ3InterpolationTestout {

	public static void main(String[] args) {

		System.out.println("--------------------------------------");
		attempt1();
		System.out.println("--------------------------------------");
	}	
	
	public static void attempt1() {
	    System.out.print("Z3 Major Version: ");
	    System.out.println(Version.getMajor());
	    System.out.print("Z3 Full Version: ");
	    System.out.println(Version.getString());
	    HashMap<String, String> cfg = new HashMap<String, String>();
	    cfg.put("proof", "true");
	    cfg.put("model", "true");
	    InterpolationContext ictx = new InterpolationContext(cfg);
	    Solver s = ictx.mkSolver();
	    // A = x > 5
	    // B = x < 5
	    //Whats Interp(A,B)?
	    IntExpr x = ictx.mkIntConst("x");
	    IntExpr five = ictx.mkInt(5);
	    BoolExpr A = ictx.mkGt(x, five);
	    BoolExpr B = ictx.mkLt(x, five);
	    BoolExpr iA = ictx.MkInterpolant(A);
	    BoolExpr pat = ictx.mkAnd(iA, B);
	    System.out.println("A: " + A);
	    System.out.println("B: " + B);
	    System.out.println("Pattern: " +  pat);
	    Params params = ictx.mkParams();
	    s.add(A);
	    s.add(B);
	    //s.add(B);
	    s.check();
	    Expr proof = s.getProof();         
	    Expr[] interps = ictx.GetInterpolant(proof, pat, params);
	    for(int i = 0; i < interps.length; i++){
	        System.out.println("Interpolant: " + interps[i]);
	    }

	}

}

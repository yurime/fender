package fsb.ast;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.SBState;
import fsb.explore.State;
import fsb.tvl.ArithValue;

public class Store extends Statement {
	AritExpr src;
	SharedVal dst;
	
	public Store(AritExpr src, SharedVal dst) {
		super(StatType.STORE);
		this.src = src;
		this.dst = dst;
	}

	public String toString()
	{
		return "STORE " + dst + " = " + src;
	}

	public ArithValue getSrcVal(State s, int pid) {
		//TODO: Ugly, ugly hack for MV.
//		ArrayList<Integer> ret = new ArrayList<Integer>(2);
//		if (src instanceof NondetArit)
//		{
//			ret.add(0);
//			ret.add(1);
//		}
//		else
//			ret.add(src.evaluate(s, pid));
		
		return src.evaluate(s, pid);//ret;
	}
	
	public int getDst(SBState s, int pid) {
		return dst.evalShared(s, pid);
	}

	@Override
	public boolean isLocal() {
		return false;
	}
	
	public ArithExpr getZ3Dst(Context ctx) {
		return ctx.mkIntConst(dst.toString());
	}
	
	public ArithExpr getZ3Src(Context ctx) throws Exception {
		return src.toZ3(ctx);
	}

	public String getDstName() {
		return dst.toString();
	}

	public AritExpr getSrc() {
		return src;
	}
}

package fsb.ast;


import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.SBState;

public class Load extends Statement {
	SharedVal src;
	int dst;
	
	public Load(SharedVal src, String dst) {
		super(StatType.LOAD);
		this.src = src;
		this.dst = SymbolTable.getOrCreateLocalVariable(dst);
	}

	public String toString()
	{
		return "LOAD " + SymbolTable.getLocalVariableName(dst) + " = " + src;
	}

	public int evaltSrc(SBState s, int pid) {
		return src.evalShared(s, pid);
	}
	
	
	public SharedVal getSrc() {
		return src;
	}
	
	public int getDst() {
		return dst;
	}

	@Override
	public boolean isLocal() {
		return false;
	}
	
	public ArithExpr getZ3Dst(Context ctx) {
		return ctx.mkIntConst(SymbolTable.getLocalVariableName(dst));
	}
	
	public ArithExpr getZ3Src(Context ctx) {
		return ctx.mkIntConst(src.toString());
	}



	public String getDstName() {
		return SymbolTable.getLocalVariableName(dst);
	}

}

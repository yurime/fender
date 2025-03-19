package fsb.ast;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;

import fsb.explore.State;
import fsb.tvl.ArithValue;

public class Assign extends Statement {

	AritExpr src;
	public AritExpr getSrc() {
		return src;
	}

	int dst;
	
	public Assign(AritExpr src, String dst) {
		super(StatType.ASSIGN);
		this.src = src;
		this.dst = SymbolTable.getOrCreateLocalVariable(dst);
	}

	public String toString()
	{
		return SymbolTable.getLocalVariableName(dst) + " = " + src.toString();
	}

	public ArithValue getSrcVal(State s, int pid) {		
		return src.evaluate(s, pid);
	}

	public int getDst() {
		return dst;
	}

	public String getDstName() {
		return  SymbolTable.getLocalVariableName(dst);
	}
	
	@Override
	public boolean isLocal() {
		return true;
	}
	
	public ArithExpr getZ3Dst(Context ctx) {
		return ctx.mkIntConst(SymbolTable.getLocalVariableName(dst));
	}
	
	public ArithExpr getZ3Src(Context ctx) {
		return src.toZ3(ctx);
	}
}

package fsb.parser;
import java_cup.runtime.Symbol;

%%
%class StatementLexer
%public
%cup
%line
%column
%x COMMENT

%{

  private Symbol symbol(int sym) {
    return new Symbol(sym, yyline+1, yycolumn+1);
  }
  
  private Symbol symbol(int sym, Object val) {
    return new Symbol(sym, yyline+1, yycolumn+1, val);
  }
  
  private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }
%} 

Ident = [a-zA-Z] [a-zA-Z0-9_]*
IntLiteral = 0 | [1-9][0-9]*
new_line = \r|\n|\r\n;
white_space = {new_line} | [ \t\f\b ]
comment_s = ("/*")
comment_f = ([*]+"/")
cpp_comment_s = ("/""/")
c_comment = {comment_s}([^*]|([*]+[^/*]))*{comment_f}
%%

{comment_s}	  { return  symbol(st_sym.C_COMMENT_S); }
{comment_f}	  { return  symbol(st_sym.C_COMMENT_F); }
{cpp_comment_s} { return  symbol(st_sym.CPP_COMMENT_S); }
"Statement"   { return symbol(st_sym.ST_RESRV_W); }
"load"		  { return symbol(st_sym.LOAD); }
"store"		  { return symbol(st_sym.STORE); }
"barrier"	  { return symbol(st_sym.BARRIER); }
"if"		  { return symbol(st_sym.IF); }
"goto"		  { return symbol(st_sym.GOTO); }
"nop"		  { return symbol(st_sym.NOP); }
"allocate"	  { return symbol(st_sym.ALLOCATE); }
"true"		  { return symbol(st_sym.TRUE); }
"false"		  { return symbol(st_sym.FALSE); }
"cas"		  { return symbol(st_sym.CAS); }
"swap"		  { return symbol(st_sym.SWAP); }
"dec"		  { return symbol(st_sym.ATOMICDEC); }
"assert"	  { return symbol(st_sym.ASSERT); }
"then"	  { return symbol(st_sym.THEN); }
"else"	  { return symbol(st_sym.ELSE); }
"final"		  { return symbol(st_sym.FINAL); }
"always"	  { return symbol(st_sym.ALWAYS); }
"begin_atomic" { return symbol(st_sym.BEGINATOMIC); }
"end_atomic"   { return symbol(st_sym.ENDATOMIC); }
"assume"      { return symbol(st_sym.ASSUME); }
"pc"		  { return symbol(st_sym.PC); }
"choose"		  { return symbol(st_sym.CHOOSE); }

{Ident}           { return symbol(st_sym.IDENT, yytext()); }
{IntLiteral}	  { return symbol(st_sym.NUMBER, new Integer(Integer.parseInt(yytext()))); }

";"		 { return symbol(st_sym.SEMI); }
"+"		 { return symbol(st_sym.PLUS); }
"-"		 { return symbol(st_sym.MINUS); }
"*"		 { return symbol(st_sym.MUL); }
"=="		 { return symbol(st_sym.EQUAL); }
"="		 { return symbol(st_sym.ASSIGN); }
"!="		 { return symbol(st_sym.NEQ); }
"<"		 { return symbol(st_sym.LESS); }
">"		 { return symbol(st_sym.GREATER); }
"<="		 { return symbol(st_sym.LE); }
">="		 { return symbol(st_sym.GE); }
":"		 { return symbol(st_sym.COLON); }
","		 { return symbol(st_sym.COMMA); }
"."		 { return symbol(st_sym.DOT); }
"["		 { return symbol(st_sym.LEFTBRACE); }
"]"		 { return symbol(st_sym.RIGHTBRACE); }
"("		 { return symbol(st_sym.LEFTPAREN); }
")"		 { return symbol(st_sym.RIGHTPAREN); }
"{"		 { return symbol(st_sym.LEFTCURLY); }
"}"		 { return symbol(st_sym.RIGHTCURLY); }
"&"	 { return symbol(st_sym.AND2); }
"&&"	 { return symbol(st_sym.AND); }
"|"	 { return symbol(st_sym.OR2); }
"||"	 { return symbol(st_sym.OR); }
"!"		 { return symbol(st_sym.NOT); }


{white_space}     { /* ignore */ }

.|\n              {  /* throw new Error("Illegal character <"+ yytext()+">");*/
		    error("Illegal character <"+ yytext()+">" + " ascii(0): " + (int)(yytext().charAt(0)) );
                  }
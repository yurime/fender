package fsb.parser;
import java_cup.runtime.Symbol;

%%
%class PredLexer
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
white_space = {new_line} | [ \t\f]
comment_s = ("/*")
comment_f = ([*]+"/")
c_comment = {comment_s}([^*]|([*]+[^/*]))*{comment_f}
%%

"true"		  { return symbol(pred_sym.TRUE); }
"false"		  { return symbol(pred_sym.FALSE); }
{Ident}           { return symbol(pred_sym.IDENT, yytext()); }
{IntLiteral}	  { return symbol(pred_sym.NUMBER, new Integer(Integer.parseInt(yytext()))); }

"+"		 { return symbol(pred_sym.PLUS); }
"-"		 { return symbol(pred_sym.MINUS); }
"*"		 { return symbol(pred_sym.MUL); }
"="		 { return symbol(pred_sym.ASSIGN); }
"!="		 { return symbol(pred_sym.NEQ); }
"<"		 { return symbol(pred_sym.LESS); }
">"		 { return symbol(pred_sym.GREATER); }
"("		 { return symbol(pred_sym.LEFTPAREN); }
")"		 { return symbol(pred_sym.RIGHTPAREN); }
"&&"	 { return symbol(pred_sym.AND); }
"||"	 { return symbol(pred_sym.OR); }
"!"		 { return symbol(pred_sym.NOT); }


{white_space}     { /* ignore */ }

.|\n              {  /* throw new Error("Illegal character <"+ yytext()+">");*/
		    error("Illegal character <"+ yytext()+">");
                  }
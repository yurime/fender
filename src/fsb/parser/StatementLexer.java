/* The following code was generated by JFlex 1.4.3 on 4/17/16 1:42 AM */

package fsb.parser;
import java_cup.runtime.Symbol;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 4/17/16 1:42 AM from the specification file
 * <tt>statement_lexer.jflex</tt>
 */
public class StatementLexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\10\0\2\10\1\6\1\0\1\10\1\5\22\0\1\10\1\43\4\0"+
    "\1\57\1\0\1\53\1\54\1\12\1\40\1\47\1\41\1\50\1\11"+
    "\1\3\11\4\1\46\1\7\1\44\1\42\1\45\2\0\22\1\1\13"+
    "\7\1\1\51\1\0\1\52\1\0\1\2\1\0\1\15\1\26\1\33"+
    "\1\23\1\16\1\30\1\31\1\36\1\27\2\1\1\21\1\17\1\20"+
    "\1\22\1\32\1\1\1\25\1\24\1\14\1\34\1\1\1\35\1\1"+
    "\1\37\1\1\1\55\1\60\1\56\42\0\1\10\uff5f\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\2\3\2\4\1\5\1\1\1\6"+
    "\16\2\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25"+
    "\1\26\1\27\1\0\1\30\1\31\1\32\1\0\16\2"+
    "\1\33\3\2\1\34\2\2\1\35\1\36\1\37\1\40"+
    "\1\41\1\42\10\2\1\43\1\2\1\44\7\2\1\45"+
    "\2\2\1\46\1\47\5\2\1\50\1\51\1\2\1\52"+
    "\4\2\1\53\7\2\1\54\2\2\1\55\1\56\3\2"+
    "\1\57\1\60\1\61\3\2\1\62\3\2\1\63\2\2"+
    "\1\64\2\2\1\65\2\2\1\66\2\2\1\67";

  private static int [] zzUnpackAction() {
    int [] result = new int[149];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\61\0\61\0\142\0\61\0\223\0\304\0\61"+
    "\0\61\0\365\0\u0126\0\u0157\0\u0188\0\u01b9\0\u01ea\0\u021b"+
    "\0\u024c\0\u027d\0\u02ae\0\u02df\0\u0310\0\u0341\0\u0372\0\u03a3"+
    "\0\u03d4\0\61\0\61\0\u0405\0\u0436\0\u0467\0\u0498\0\61"+
    "\0\61\0\61\0\61\0\61\0\61\0\61\0\61\0\61"+
    "\0\u04c9\0\u04fa\0\u052b\0\61\0\61\0\61\0\u0126\0\u055c"+
    "\0\u058d\0\u05be\0\u05ef\0\u0620\0\u0651\0\u0682\0\u06b3\0\u06e4"+
    "\0\u0715\0\u0746\0\u0777\0\u07a8\0\u07d9\0\142\0\u080a\0\u083b"+
    "\0\u086c\0\142\0\u089d\0\u08ce\0\61\0\61\0\61\0\61"+
    "\0\61\0\61\0\u08ff\0\u0930\0\u0961\0\u0992\0\u09c3\0\u09f4"+
    "\0\u0a25\0\u0a56\0\142\0\u0a87\0\142\0\u0ab8\0\u0ae9\0\u0b1a"+
    "\0\u0b4b\0\u0b7c\0\u0bad\0\u0bde\0\142\0\u0c0f\0\u0c40\0\142"+
    "\0\142\0\u0c71\0\u0ca2\0\u0cd3\0\u0d04\0\u0d35\0\142\0\142"+
    "\0\u0d66\0\142\0\u0d97\0\u0dc8\0\u0df9\0\u0e2a\0\142\0\u0e5b"+
    "\0\u0e8c\0\u0ebd\0\u0eee\0\u0f1f\0\u0f50\0\u0f81\0\142\0\u0fb2"+
    "\0\u0fe3\0\142\0\142\0\u1014\0\u1045\0\u1076\0\142\0\142"+
    "\0\142\0\u10a7\0\u10d8\0\u1109\0\142\0\u113a\0\u116b\0\u119c"+
    "\0\142\0\u11cd\0\u11fe\0\142\0\u122f\0\u1260\0\142\0\u1291"+
    "\0\u12c2\0\142\0\u12f3\0\u1324\0\142";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[149];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\3\1\5\1\6\1\7\1\10\1\11"+
    "\1\10\1\12\1\13\1\14\1\15\1\16\1\17\1\4"+
    "\1\20\1\21\1\4\1\22\1\23\1\4\1\24\1\25"+
    "\1\26\1\27\1\30\1\31\4\4\1\32\1\33\1\34"+
    "\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44"+
    "\1\45\1\46\1\47\1\50\1\51\1\52\62\0\4\4"+
    "\6\0\25\4\24\0\2\6\62\0\1\53\63\0\1\54"+
    "\1\55\57\0\1\56\1\57\47\0\4\4\6\0\1\4"+
    "\1\60\23\4\22\0\4\4\6\0\12\4\1\61\10\4"+
    "\1\62\1\4\22\0\4\4\6\0\6\4\1\63\2\4"+
    "\1\64\13\4\22\0\4\4\6\0\5\4\1\65\1\66"+
    "\16\4\22\0\4\4\6\0\7\4\1\67\15\4\22\0"+
    "\4\4\6\0\7\4\1\70\15\4\22\0\4\4\6\0"+
    "\3\4\1\71\21\4\22\0\4\4\6\0\1\4\1\72"+
    "\20\4\1\73\2\4\22\0\4\4\6\0\2\4\1\74"+
    "\1\75\21\4\22\0\4\4\6\0\15\4\1\76\7\4"+
    "\22\0\4\4\6\0\2\4\1\77\11\4\1\100\10\4"+
    "\22\0\4\4\6\0\7\4\1\101\15\4\22\0\4\4"+
    "\6\0\20\4\1\102\4\4\22\0\4\4\6\0\2\4"+
    "\1\103\20\4\1\104\1\4\63\0\1\105\60\0\1\106"+
    "\60\0\1\107\60\0\1\110\75\0\1\111\61\0\1\112"+
    "\7\0\1\10\52\0\4\4\6\0\2\4\1\113\22\4"+
    "\22\0\4\4\6\0\21\4\1\114\3\4\22\0\4\4"+
    "\6\0\3\4\1\115\21\4\22\0\4\4\6\0\6\4"+
    "\1\116\13\4\1\117\2\4\22\0\4\4\6\0\11\4"+
    "\1\120\13\4\22\0\4\4\6\0\10\4\1\121\14\4"+
    "\22\0\4\4\6\0\11\4\1\122\13\4\22\0\4\4"+
    "\6\0\17\4\1\123\5\4\22\0\4\4\6\0\2\4"+
    "\1\124\22\4\22\0\4\4\6\0\20\4\1\125\4\4"+
    "\22\0\4\4\6\0\7\4\1\126\15\4\22\0\4\4"+
    "\6\0\2\4\1\127\22\4\22\0\4\4\6\0\12\4"+
    "\1\130\12\4\22\0\4\4\6\0\16\4\1\131\6\4"+
    "\22\0\4\4\6\0\6\4\1\132\16\4\22\0\4\4"+
    "\6\0\5\4\1\133\17\4\22\0\4\4\6\0\1\4"+
    "\1\134\23\4\22\0\4\4\6\0\11\4\1\135\13\4"+
    "\22\0\4\4\6\0\7\4\1\136\15\4\22\0\4\4"+
    "\6\0\1\4\1\137\23\4\22\0\4\4\6\0\3\4"+
    "\1\140\21\4\22\0\4\4\6\0\5\4\1\141\17\4"+
    "\22\0\4\4\6\0\7\4\1\142\15\4\22\0\4\4"+
    "\6\0\2\4\1\143\22\4\22\0\4\4\6\0\3\4"+
    "\1\144\15\4\1\145\3\4\22\0\1\4\1\146\2\4"+
    "\6\0\25\4\22\0\4\4\6\0\3\4\1\147\21\4"+
    "\22\0\4\4\6\0\10\4\1\150\14\4\22\0\4\4"+
    "\6\0\12\4\1\151\12\4\22\0\4\4\6\0\17\4"+
    "\1\152\5\4\22\0\4\4\6\0\12\4\1\153\12\4"+
    "\22\0\4\4\6\0\14\4\1\154\10\4\22\0\4\4"+
    "\6\0\11\4\1\155\13\4\22\0\4\4\6\0\2\4"+
    "\1\156\22\4\22\0\4\4\6\0\7\4\1\157\15\4"+
    "\22\0\4\4\6\0\7\4\1\160\15\4\22\0\4\4"+
    "\6\0\3\4\1\161\21\4\22\0\4\4\6\0\20\4"+
    "\1\162\4\4\22\0\4\4\6\0\24\4\1\163\22\0"+
    "\4\4\6\0\12\4\1\164\12\4\22\0\4\4\6\0"+
    "\4\4\1\165\20\4\22\0\4\4\6\0\2\4\1\166"+
    "\22\4\22\0\4\4\6\0\3\4\1\167\21\4\22\0"+
    "\4\4\6\0\14\4\1\170\10\4\22\0\4\4\6\0"+
    "\5\4\1\171\17\4\22\0\4\4\6\0\3\4\1\172"+
    "\21\4\22\0\4\4\6\0\6\4\1\173\16\4\22\0"+
    "\4\4\6\0\11\4\1\174\13\4\22\0\4\4\6\0"+
    "\4\4\1\175\20\4\22\0\4\4\6\0\2\4\1\176"+
    "\22\4\22\0\4\4\6\0\11\4\1\177\13\4\22\0"+
    "\4\4\6\0\1\4\1\200\23\4\22\0\4\4\6\0"+
    "\3\4\1\201\21\4\22\0\4\4\6\0\1\4\1\202"+
    "\23\4\22\0\4\4\6\0\3\4\1\203\21\4\22\0"+
    "\1\4\1\204\2\4\6\0\25\4\22\0\4\4\6\0"+
    "\3\4\1\205\21\4\22\0\4\4\6\0\3\4\1\206"+
    "\21\4\22\0\4\4\6\0\1\4\1\207\23\4\22\0"+
    "\4\4\6\0\7\4\1\210\15\4\22\0\4\4\6\0"+
    "\12\4\1\211\12\4\22\0\4\4\6\0\2\4\1\212"+
    "\22\4\22\0\4\4\6\0\5\4\1\213\17\4\22\0"+
    "\4\4\6\0\3\4\1\214\21\4\22\0\4\4\6\0"+
    "\4\4\1\215\20\4\22\0\4\4\6\0\1\4\1\216"+
    "\23\4\22\0\4\4\6\0\1\4\1\217\23\4\22\0"+
    "\4\4\6\0\14\4\1\220\10\4\22\0\4\4\6\0"+
    "\7\4\1\221\15\4\22\0\4\4\6\0\20\4\1\222"+
    "\4\4\22\0\4\4\6\0\4\4\1\223\20\4\22\0"+
    "\4\4\6\0\14\4\1\224\10\4\22\0\4\4\6\0"+
    "\20\4\1\225\4\4\21\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4949];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\10\1\11\1\1\1\11\2\1\2\11\20\1"+
    "\2\11\4\1\11\11\2\1\1\0\3\11\1\0\25\1"+
    "\6\11\113\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[149];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */

  private Symbol symbol(int sym) {
    return new Symbol(sym, yyline+1, yycolumn+1);
  }
  
  private Symbol symbol(int sym, Object val) {
    return new Symbol(sym, yyline+1, yycolumn+1, val);
  }
  
  private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public StatementLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public StatementLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 136) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 48: 
          { return symbol(st_sym.ASSERT);
          }
        case 56: break;
        case 4: 
          { /* ignore */
          }
        case 57: break;
        case 16: 
          { return symbol(st_sym.LEFTBRACE);
          }
        case 58: break;
        case 23: 
          { return symbol(st_sym.OR2);
          }
        case 59: break;
        case 9: 
          { return symbol(st_sym.ASSIGN);
          }
        case 60: break;
        case 24: 
          { return  symbol(st_sym.CPP_COMMENT_S);
          }
        case 61: break;
        case 47: 
          { return symbol(st_sym.ALWAYS);
          }
        case 62: break;
        case 36: 
          { return symbol(st_sym.ATOMICDEC);
          }
        case 63: break;
        case 8: 
          { return symbol(st_sym.MINUS);
          }
        case 64: break;
        case 5: 
          { return symbol(st_sym.SEMI);
          }
        case 65: break;
        case 10: 
          { return symbol(st_sym.NOT);
          }
        case 66: break;
        case 33: 
          { return symbol(st_sym.AND);
          }
        case 67: break;
        case 27: 
          { return symbol(st_sym.IF);
          }
        case 68: break;
        case 51: 
          { return symbol(st_sym.BARRIER);
          }
        case 69: break;
        case 34: 
          { return symbol(st_sym.OR);
          }
        case 70: break;
        case 20: 
          { return symbol(st_sym.LEFTCURLY);
          }
        case 71: break;
        case 14: 
          { return symbol(st_sym.COMMA);
          }
        case 72: break;
        case 49: 
          { return symbol(st_sym.ASSUME);
          }
        case 73: break;
        case 1: 
          { /* throw new Error("Illegal character <"+ yytext()+">");*/
		    error("Illegal character <"+ yytext()+">" + " ascii(0): " + (int)(yytext().charAt(0)) );
          }
        case 74: break;
        case 22: 
          { return symbol(st_sym.AND2);
          }
        case 75: break;
        case 45: 
          { return symbol(st_sym.FALSE);
          }
        case 76: break;
        case 17: 
          { return symbol(st_sym.RIGHTBRACE);
          }
        case 77: break;
        case 54: 
          { return symbol(st_sym.ENDATOMIC);
          }
        case 78: break;
        case 43: 
          { return symbol(st_sym.GOTO);
          }
        case 79: break;
        case 28: 
          { return symbol(st_sym.PC);
          }
        case 80: break;
        case 18: 
          { return symbol(st_sym.LEFTPAREN);
          }
        case 81: break;
        case 30: 
          { return symbol(st_sym.NEQ);
          }
        case 82: break;
        case 52: 
          { return symbol(st_sym.ALLOCATE);
          }
        case 83: break;
        case 41: 
          { return symbol(st_sym.LOAD);
          }
        case 84: break;
        case 25: 
          { return  symbol(st_sym.C_COMMENT_S);
          }
        case 85: break;
        case 50: 
          { return symbol(st_sym.CHOOSE);
          }
        case 86: break;
        case 32: 
          { return symbol(st_sym.GE);
          }
        case 87: break;
        case 53: 
          { return symbol(st_sym.ST_RESRV_W);
          }
        case 88: break;
        case 42: 
          { return symbol(st_sym.SWAP);
          }
        case 89: break;
        case 31: 
          { return symbol(st_sym.LE);
          }
        case 90: break;
        case 37: 
          { return symbol(st_sym.CAS);
          }
        case 91: break;
        case 35: 
          { return symbol(st_sym.NOP);
          }
        case 92: break;
        case 21: 
          { return symbol(st_sym.RIGHTCURLY);
          }
        case 93: break;
        case 46: 
          { return symbol(st_sym.FINAL);
          }
        case 94: break;
        case 2: 
          { return symbol(st_sym.IDENT, yytext());
          }
        case 95: break;
        case 19: 
          { return symbol(st_sym.RIGHTPAREN);
          }
        case 96: break;
        case 7: 
          { return symbol(st_sym.PLUS);
          }
        case 97: break;
        case 6: 
          { return symbol(st_sym.MUL);
          }
        case 98: break;
        case 11: 
          { return symbol(st_sym.LESS);
          }
        case 99: break;
        case 38: 
          { return symbol(st_sym.TRUE);
          }
        case 100: break;
        case 12: 
          { return symbol(st_sym.GREATER);
          }
        case 101: break;
        case 13: 
          { return symbol(st_sym.COLON);
          }
        case 102: break;
        case 15: 
          { return symbol(st_sym.DOT);
          }
        case 103: break;
        case 40: 
          { return symbol(st_sym.ELSE);
          }
        case 104: break;
        case 55: 
          { return symbol(st_sym.BEGINATOMIC);
          }
        case 105: break;
        case 3: 
          { return symbol(st_sym.NUMBER, new Integer(Integer.parseInt(yytext())));
          }
        case 106: break;
        case 44: 
          { return symbol(st_sym.STORE);
          }
        case 107: break;
        case 29: 
          { return symbol(st_sym.EQUAL);
          }
        case 108: break;
        case 26: 
          { return  symbol(st_sym.C_COMMENT_F);
          }
        case 109: break;
        case 39: 
          { return symbol(st_sym.THEN);
          }
        case 110: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}

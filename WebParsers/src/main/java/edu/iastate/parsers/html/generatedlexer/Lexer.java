/* The following code was generated by JFlex 1.4.3 on 12/21/14 9:48 PM */

package edu.iastate.parsers.html.generatedlexer;
     

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 12/21/14 9:48 PM from the specification file
 * <tt>html.jflex</tt>
 */
public class Lexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int QT_APOS = 6;
  public static final int SCRIPT = 12;
  public static final int EQ = 4;
  public static final int YYINITIAL = 0;
  public static final int COMMENT = 14;
  public static final int ATTR_VAL_APOS = 10;
  public static final int ATTR_VAL_QT = 8;
  public static final int ATTR_NAME = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7, 7
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\2\4\1\0\2\4\22\0\1\4\1\21\1\11\4\0\1\3"+
    "\5\0\1\22\1\0\1\6\12\2\2\0\1\5\1\10\1\7\2\0"+
    "\2\1\1\14\5\1\1\16\6\1\1\17\1\1\1\15\1\13\1\20"+
    "\6\1\1\0\1\12\2\0\1\1\1\0\2\1\1\14\5\1\1\16"+
    "\6\1\1\17\1\1\1\15\1\13\1\20\6\1\uff85\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\1\3\0\2\2\1\1\1\0\2\1\1\3\1\4"+
    "\1\5\1\3\1\6\1\7\1\10\1\11\1\12\1\13"+
    "\2\2\1\14\3\2\1\1\2\5\1\15\2\0\1\16"+
    "\1\2\5\0\1\17\1\20\1\21\5\0\1\22";

  private static int [] zzUnpackAction() {
    int [] result = new int[48];
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
    "\0\0\0\23\0\46\0\71\0\114\0\137\0\162\0\205"+
    "\0\230\0\253\0\276\0\321\0\344\0\367\0\276\0\u010a"+
    "\0\276\0\u011d\0\276\0\276\0\u0130\0\u0143\0\276\0\u0156"+
    "\0\u0169\0\u017c\0\u018f\0\276\0\u01a2\0\u01b5\0\u01c8\0\u01db"+
    "\0\276\0\276\0\u01ee\0\u0201\0\u0214\0\u0227\0\u023a\0\276"+
    "\0\276\0\276\0\u024d\0\u0260\0\u0273\0\u0286\0\u0299\0\276";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[48];
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
    "\5\11\1\12\15\11\1\13\1\14\2\13\1\15\1\13"+
    "\1\16\1\17\3\13\6\14\3\13\1\20\2\13\1\15"+
    "\1\13\1\16\1\17\1\21\2\13\6\20\2\13\3\22"+
    "\1\23\1\15\1\22\1\16\1\17\1\22\1\24\11\22"+
    "\6\25\1\26\1\17\1\25\1\27\1\30\10\25\3\31"+
    "\1\27\2\31\1\32\1\17\2\31\1\30\10\31\5\11"+
    "\1\33\15\11\22\34\1\35\5\11\1\0\15\11\1\0"+
    "\1\36\4\0\1\37\4\0\6\36\1\40\25\0\2\14"+
    "\10\0\6\14\1\0\1\14\4\0\1\15\25\0\1\41"+
    "\14\0\2\20\10\0\6\20\1\0\1\20\3\22\2\0"+
    "\1\22\2\0\1\22\1\0\11\22\7\25\1\0\1\25"+
    "\2\0\17\25\1\41\1\25\2\0\10\25\23\42\3\31"+
    "\1\0\3\31\1\0\2\31\1\0\13\31\1\0\3\31"+
    "\1\41\2\31\1\0\10\31\6\0\1\43\36\0\1\44"+
    "\1\0\2\36\10\0\6\36\1\0\1\36\1\0\1\45"+
    "\11\0\6\45\24\0\1\46\13\0\1\47\16\0\1\50"+
    "\14\0\2\45\4\0\1\51\3\0\6\45\1\0\1\45"+
    "\22\0\1\52\14\0\1\53\23\0\1\54\23\0\1\55"+
    "\23\0\1\56\23\0\1\57\11\0\1\60\13\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[684];
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
    "\1\1\3\0\3\1\1\0\2\1\1\11\3\1\1\11"+
    "\1\1\1\11\1\1\2\11\2\1\1\11\4\1\1\11"+
    "\2\1\2\0\2\11\5\0\3\11\5\0\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[48];
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
	private String currentOpenTag = null;
	
	public void setCurrentOpenTag(String currentOpenTag) {
		this.currentOpenTag = currentOpenTag;
	}
	
	public String getCurrentOpenTag() {
		return currentOpenTag;
	}
	
	private String getState(int state) {
		if (state == YYINITIAL)
			return "YYINITIAL";
		else if (state == ATTR_NAME)
			return "ATTR_NAME";
		else if (state == EQ)
			return "EQ";
		else if (state == QT_APOS)
			return "QT_APOS";
		else if (state == ATTR_VAL_QT)
			return "ATTR_VAL_QT";
		else if (state == ATTR_VAL_APOS)
			return "ATTR_VAL_APOS";
		else if (state == SCRIPT)
			return "SCRIPT";
		else // if (state == COMMENT)
			return "COMMENT";
	}
	


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexer(java.io.InputStream in) {
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
    while (i < 96) {
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
  public Token nextToken() throws java.io.IOException {
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

      yychar+= zzMarkedPosL-zzStartRead;

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
        case 2: 
          { return new Token(Token.Type.AttrValFrag, yytext(), yychar);
          }
        case 19: break;
        case 13: 
          { String tagName = yytext().substring(1); 
							  currentOpenTag = tagName;
							  yybegin(ATTR_NAME); 		return new Token(Token.Type.OpenTag, yytext(), yychar, tagName);
          }
        case 20: break;
        case 15: 
          { yybegin(YYINITIAL);
          }
        case 21: break;
        case 8: 
          { yybegin(QT_APOS); 		return new Token(Token.Type.Eq, yytext(), yychar);
          }
        case 22: break;
        case 17: 
          { yybegin(COMMENT);
          }
        case 23: break;
        case 10: 
          { yybegin(ATTR_VAL_APOS);	return new Token(Token.Type.AttrValStart, yytext(), yychar);
          }
        case 24: break;
        case 3: 
          { System.out.println("HTML Parser Error: Unexpected character [" + yytext() + "] in state " + getState(yystate()) + ".");
          }
        case 25: break;
        case 7: 
          { return new Token(Token.Type.AttrName, yytext(), yychar);
          }
        case 26: break;
        case 18: 
          { String tagName = yytext().substring(2, yytext().length() - 1);
							  yybegin(YYINITIAL);		return new Token(Token.Type.CloseTag, yytext(), yychar, tagName);
          }
        case 27: break;
        case 6: 
          { if (currentOpenTag != null && currentOpenTag.toLowerCase().equals("script")) 
									yybegin(SCRIPT);
							  else
							    	yybegin(YYINITIAL);
							  currentOpenTag = null;
							    						return new Token(Token.Type.OpenTagEnd, yytext(), yychar);
          }
        case 28: break;
        case 4: 
          { yybegin(EQ); 				return new Token(Token.Type.AttrName, yytext(), yychar);
          }
        case 29: break;
        case 12: 
          { yybegin(ATTR_NAME);		return new Token(Token.Type.AttrValEnd, yytext(), yychar);
          }
        case 30: break;
        case 9: 
          { yybegin(ATTR_NAME);		return new Token(Token.Type.AttrValue, yytext(), yychar);
          }
        case 31: break;
        case 1: 
          { return new Token(Token.Type.Text, yytext(), yychar);
          }
        case 32: break;
        case 11: 
          { yybegin(ATTR_VAL_QT); 	return new Token(Token.Type.AttrValStart, yytext(), yychar);
          }
        case 33: break;
        case 5: 
          { 
          }
        case 34: break;
        case 14: 
          { currentOpenTag = null;
							  yybegin(YYINITIAL);		return new Token(Token.Type.OpenTagEnd, yytext(), yychar);
          }
        case 35: break;
        case 16: 
          { String tagName = yytext().substring(2, yytext().length() - 1);
														return new Token(Token.Type.CloseTag, yytext(), yychar, tagName);
          }
        case 36: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { 	return null;
 }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}

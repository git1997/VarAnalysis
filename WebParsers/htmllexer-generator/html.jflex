package edu.iastate.parsers.html.generatedlexer;
     
%%

%public   
%class Lexer

%function nextToken
%type Token

%char
%ignorecase

%eofval{
	return null;
%eofval}
%eofclose

%{
	private String currentOpenTag = null;
	
	public void setCurrentOpenTag(String currentOpenTag) {
		this.currentOpenTag = currentOpenTag;
	}
	
	public String getCurrentOpenTag() {
		return currentOpenTag;
	}
	
	public static String getState(int state) {
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
	
%}

/*==================== Regular Expressions ====================*/

SimpleName 	=	[A-Za-z_][A-Za-z0-9\-_]*
SimpleValue =	[^ \t\r\n\f\"'>/]+ 
WhiteSpace 	=	[ \t\r\n\f]					

/*========================== States ===========================*/

%state ATTR_NAME, EQ, QT_APOS, ATTR_VAL_QT, ATTR_VAL_APOS, SCRIPT, COMMENT
   
%%

/*======================= Lexical Rules =======================*/
 
<YYINITIAL> {
	"<"{SimpleName}			{ String tagName = yytext().substring(1); 
							  currentOpenTag = tagName;
							  yybegin(ATTR_NAME); 		return new Token(Token.Type.OpenTag, yytext(), yychar, tagName); }
	
	"</"{SimpleName}">"		{ String tagName = yytext().substring(2, yytext().length() - 1);
														return new Token(Token.Type.CloseTag, yytext(), yychar, tagName); }
	
	[^"<"]* | "<"			{ 							return new Token(Token.Type.Text, yytext(), yychar); }
}

<ATTR_NAME> {
	{SimpleName}			{ yybegin(EQ); 				return new Token(Token.Type.AttrName, yytext(), yychar); }
}

<EQ> {
	"="						{ yybegin(QT_APOS); 		return new Token(Token.Type.Eq, yytext(), yychar); }
	{SimpleName}			{ 							return new Token(Token.Type.AttrName, yytext(), yychar); }
}

<QT_APOS> {
	\"						{ yybegin(ATTR_VAL_QT); 	return new Token(Token.Type.AttrValStart, yytext(), yychar); }
	'						{ yybegin(ATTR_VAL_APOS);	return new Token(Token.Type.AttrValStart, yytext(), yychar); }
	{SimpleValue}			{ yybegin(ATTR_NAME);		return new Token(Token.Type.AttrValue, yytext(), yychar); }	
}

<ATTR_VAL_QT> {
	[^\\\">]*				{ 							return new Token(Token.Type.AttrValFrag, yytext(), yychar); }
	\\[^]					{							return new Token(Token.Type.AttrValFrag, yytext(), yychar); }
	\\						{							return new Token(Token.Type.AttrValFrag, yytext(), yychar); }
	\"						{ yybegin(ATTR_NAME);		return new Token(Token.Type.AttrValEnd, yytext(), yychar); }
}

<ATTR_VAL_APOS> {
	[^\\'>]*				{ 							return new Token(Token.Type.AttrValFrag, yytext(), yychar); }
	\\[^]					{							return new Token(Token.Type.AttrValFrag, yytext(), yychar); }
	\\						{							return new Token(Token.Type.AttrValFrag, yytext(), yychar); }
	'						{ yybegin(ATTR_NAME);		return new Token(Token.Type.AttrValEnd, yytext(), yychar); }
}

<ATTR_NAME, EQ, QT_APOS, ATTR_VAL_QT, ATTR_VAL_APOS> {
	{WhiteSpace}+			{ }
	
	">"						{ if (currentOpenTag != null && currentOpenTag.toLowerCase().equals("script")) 
									yybegin(SCRIPT);
							  else
							    	yybegin(YYINITIAL);
							  currentOpenTag = null;
							    						return new Token(Token.Type.OpenTagEnd, yytext(), yychar); }
							    
	"/>"					{ currentOpenTag = null;
							  yybegin(YYINITIAL);		return new Token(Token.Type.OpenTagEnd, yytext(), yychar); }
	
	[^]						{ System.out.println("HTML Parser Error: Unexpected character [" + yytext() + "] in state " + getState(yystate()) + "."); } 
}

<SCRIPT> {
	"</script>"				{ String tagName = yytext().substring(2, yytext().length() - 1);
							  yybegin(YYINITIAL);		return new Token(Token.Type.CloseTag, yytext(), yychar, tagName); }
														
	[^"<"]* | "<"			{ 							return new Token(Token.Type.Text, yytext(), yychar); }
}

<YYINITIAL> {
	"<!--"					{ yybegin(COMMENT); }
} 

<COMMENT> {
	"-->"					{ yybegin(YYINITIAL); }
	[^]						{ }
}
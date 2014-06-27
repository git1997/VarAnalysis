package htmllexer;
     
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
	private String currentOpeningTag = "";
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
	"<"{SimpleName}			{ String tagName = yytext().substring(1); currentOpeningTag = tagName;	
							  yybegin(ATTR_NAME); 		return new Token(Token.Type.OpeningTag, yytext(), yychar, tagName); }
	
	"</"{SimpleName}">"		{ String tagName = yytext().substring(2, yytext().length() - 1);
														return new Token(Token.Type.ClosingTag, yytext(), yychar, tagName); }
	
	[^"<"]* | "<"			{ 							return new Token(Token.Type.Text, yytext(), yychar); }
}

<ATTR_NAME> {
	{SimpleName}			{ yybegin(EQ); 				return new Token(Token.Type.AttrName, yytext(), yychar); }
}

<EQ> {
	"="						{ yybegin(QT_APOS); }
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
	
	">"						{ if (currentOpeningTag.toLowerCase().equals("script")) 
									yybegin(SCRIPT);
							  else
							    	yybegin(YYINITIAL); }
							    
	"/>"					{ yybegin(YYINITIAL);		return new Token(Token.Type.ClosingTag, yytext(), yychar, currentOpeningTag); }
	
	[^]						{ System.out.println("HTML Parser Error: Unexpected character [" + yytext() + "] in state " + yystate() + "."); } 
}

<SCRIPT> {
	"</script>"				{ String tagName = yytext().substring(2, yytext().length() - 1);
							  yybegin(YYINITIAL);		return new Token(Token.Type.ClosingTag, yytext(), yychar, tagName); }
														
	[^"<"]* | "<"			{ 							return new Token(Token.Type.Text, yytext(), yychar); }
}

<YYINITIAL> {
	"<!--"					{ yybegin(COMMENT); }
} 

<COMMENT> {
	"-->"					{ yybegin(YYINITIAL); }
	[^]						{ }
}
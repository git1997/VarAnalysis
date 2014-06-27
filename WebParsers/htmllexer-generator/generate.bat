cd "C:\Eclipse\workspace\lab\Html Partial Parser\generator"

java -jar JFlex-1.4.3.jar html.jflex

del /F /Q "..\src\htmllexer\Lexer.java"
del /F /Q "..\bin\htmllexer\Lexer.class"

move Lexer.java "..\src\htmllexer"
"C:\Program Files\Java\jdk1.7.0\bin\javac" -cp "..\src" -d "..\bin" "..\src\htmllexer\Lexer.java"

pause
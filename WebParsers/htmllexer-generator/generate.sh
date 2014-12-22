java -jar JFlex-1.4.3.jar html.jflex

mv Lexer.java "../src/main/java/edu/iastate/parsers/html/generatedlexer/"

javac -cp "../src/main/java/" -d "../bin" "../src/main/java/edu/iastate/parsers/html/generatedlexer/Lexer.java"
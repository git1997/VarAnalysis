//package varanalysis;
//
//import org.eclipse.php.internal.core.PHPVersion;
//import org.eclipse.php.internal.core.ast.nodes.ASTParser;
//import org.eclipse.php.internal.core.ast.nodes.InLineHtml;
//import org.eclipse.php.internal.core.ast.nodes.Program;
//import org.eclipse.php.internal.core.ast.nodes.Scalar;
//import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;
//
//import php.TraceTable;
//import php.nodes.InLineHtmlNode;
//import php.nodes.ScalarNode;
//import util.FileIO;
//import util.logging.MyLevel;
//import util.logging.MyLogger;
//
///**
// * 
// * @author HUNG
// *
// */
//public class CountOutputCharsInProject {
//
//	public static String PROJECT_NAME 	= 
//				"addressbookv6.2.12";
////				"SchoolMate-1.5.4";
////				"TimeClock-1.04";
////				"UPB-2.2.7";
////				"webchess-1.0.0";
//	
//	private static String projectFolder = "/Work/To-do/Data/Web Projects/Server Code/" + PROJECT_NAME;
//	
//	public static void main(String[] args) {
//		int numOutputChars = 0;
//		int numOutputCharsOnEcho = 0;
//		for (String file : FileIO.getAllFilesInFolderByExtensions(projectFolder, new String[]{".php"})) {
////			if (file.contains("admin"))
////				continue;
//			
//			Program program = parseFile(file);
//			int cnt = countOutputCharsInProgram(program, false);
//			int cnt2 = countOutputCharsInProgram(program, true);
//			
//			System.out.println(cnt + " " + file);
//			numOutputChars += cnt;
//			numOutputCharsOnEcho += cnt2;
//		}
//		System.out.println("Number of output chars: " + numOutputChars);
//		System.out.println("Number of output chars on echo: " + numOutputCharsOnEcho);
//		System.out.format("%% output chars on echo: %d%%\n", numOutputCharsOnEcho * 100 / numOutputChars);
//	}
//	
//	private static Program parseFile(String file) {
//		// Parse the source file
//		try {
//			ASTParser parser = ASTParser.newParser(PHPVersion.PHP5, true);
//			char[] source = FileIO.readStringFromFile(file).toCharArray();
//			parser.setSource(source);
//			Program program = parser.createAST(null);
//			TraceTable.resetStaticFields();
//			TraceTable.setSourceCodeForPhpProgram(program, source);
//			return program;
//		} catch (Exception e) {
//			MyLogger.log(MyLevel.JAVA_EXCEPTION, "In FileNode.java: Error parsing " + file + " (" + e.getMessage() + ")");
//			return null;
//		}
//	}
//	
//	private static int countOutputCharsInProgram(Program program, boolean onEcho) {
//		MyVisitor myVisitor = new MyVisitor(onEcho);
//		program.accept(myVisitor);
//		return myVisitor.getNumOutputChars();
//	}
//	
//	public static class MyVisitor extends AbstractVisitor {
//		
//		private int numOutputChars = 0;
//		private boolean onEcho;
//		
//		public MyVisitor(boolean onEcho) {
//			this.onEcho = onEcho;
//		}
//		
//		public int getNumOutputChars() {
//			return numOutputChars;
//		}
//		
//		public boolean visit(InLineHtml inlineHtml) {
//			InLineHtmlNode node = new InLineHtmlNode(inlineHtml);
//			if (node.getStringValue().contains("<")) {
//				numOutputChars += 1;//node.getStringValue().length();
//			}
//			return true;
//		}
//		
//		public boolean visit(Scalar scalar) {
//			if (scalar.getStringValue().contains("<")) {
//				if (!onEcho || new ScalarNode(scalar).getLiteralType().equals("I"))
//					numOutputChars += 1;//scalar.getStringValue().length();
//			}
//			return true;
//		}
//		
//	}
//
//}

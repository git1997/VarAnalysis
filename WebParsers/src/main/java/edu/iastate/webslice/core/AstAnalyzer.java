package edu.iastate.webslice.core;

import java.io.File;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.php.internal.core.ast.nodes.ASTNode;
import org.eclipse.php.internal.core.ast.nodes.EchoStatement;
import org.eclipse.php.internal.core.ast.nodes.FunctionDeclaration;
import org.eclipse.php.internal.core.ast.nodes.FunctionInvocation;
import org.eclipse.php.internal.core.ast.nodes.InLineHtml;
import org.eclipse.php.internal.core.ast.nodes.Program;
import org.eclipse.php.internal.core.ast.nodes.Scalar;
import org.eclipse.php.internal.core.ast.visitor.AbstractVisitor;

import edu.iastate.symex.php.nodes.FileNode;
import edu.iastate.symex.position.Position;
import edu.iastate.symex.position.Range;
import edu.iastate.symex.util.ASTHelper;

/**
 * 
 * @author HUNG
 *
 */
public class AstAnalyzer {
	
	public static AstAnalyzer inst = new AstAnalyzer();
	
	private HashSet<Program> programs = new HashSet<Program>();
	private RegionManager functionRegionManager = new RegionManager();
	private RegionManager inLineHtmlRegionManager = new RegionManager();
	private RegionManager scalarRegionManager = new RegionManager();
	private RegionManager echoPrintRegionManager = new RegionManager();
	
	/**
	 * Returns information about a position
	 * @param position
	 */
	public PositionInfo getPositionInfo(Position position) {
		// TODO [ADHOC CODE]: Quick fix to parse JavaScript files as PHP files
		if (position.getFileName().endsWith(".js"))
			new FileNode(position.getFile());
		
		final File file = position.getFile();
		Program program = ASTHelper.inst.getPhpProgramOfPhpFile(file);
		
		if (!programs.contains(program)) {
			programs.add(program);
			program.accept(new AbstractVisitor() {
				
				@Override 
				public boolean visit(FunctionDeclaration functionDeclaration) {
					functionRegionManager.add(createRegion(functionDeclaration), functionDeclaration);
					return true;
				}
				
				@Override
				public boolean visit(InLineHtml inLineHtml) {
					inLineHtmlRegionManager.add(createRegion(inLineHtml), inLineHtml);
					return true;
				}
				
				@Override
				public boolean visit(Scalar scalar) {
					scalarRegionManager.add(createRegion(scalar), scalar);
					return true;
				}
				
				@Override
				public boolean visit(EchoStatement echoStatement) {
					echoPrintRegionManager.add(createRegion(echoStatement), echoStatement);
					return true;
				}
				
				@Override 
				public boolean visit(FunctionInvocation functionInvocation) {
					if (ASTHelper.inst.getSourceCodeOfPhpASTNode(functionInvocation.getFunctionName().getName()).equals("print"))
						echoPrintRegionManager.add(createRegion(functionInvocation), functionInvocation);
					return true;
				}
				
				private Region createRegion(ASTNode astNode) {
					Range range = new Range(file, astNode.getStart(), astNode.getEnd() - astNode.getStart());
					return new Region(range);
				}
				
			});
		}
		
		ASTNode functionDeclaration = functionRegionManager.findContainingAstNode(position);
		ASTNode inLineHtml = inLineHtmlRegionManager.findContainingAstNode(position);
		ASTNode scalar = scalarRegionManager.findContainingAstNode(position);
		ASTNode echoPrintStatement = echoPrintRegionManager.findContainingAstNode(position);
		
		return new PositionInfo(functionDeclaration, inLineHtml, scalar, echoPrintStatement);
	}
	
	/**
	 * This class is used to map a region to the corresponding AST node.
	 */
	private class RegionManager {
		
		private TreeMap<Region, ASTNode> regions = new TreeMap<Region, ASTNode>();
		
		public void add(Region region, ASTNode astNode) {
			regions.put(region, astNode);
		}
		
		/**
		 * Returns the AST node containing a position.
		 */
		public ASTNode findContainingAstNode(Position position) {
			Entry<Region, ASTNode> entry = regions.floorEntry(new Region(new Range(position.getFile(), position.getOffset() + 1, 0)));
			if (entry != null && entry.getKey().contains(position))
				return entry.getValue();
			else
				return null;
		}
		
	}
	
	/**
	 * This class represents a region (range) of source code.
	 */
	private class Region implements Comparable<Region> {
		
		private Range range;
		
		public Region(Range range) {
			this.range = range;
		}
		
		/**
		 * Returns true if the region contains a position
		 */
		public boolean contains(Position position) {
			return (range.getFile().equals(position.getFile())
					&& range.getOffset() <= position.getOffset()
					&& range.getOffset() + range.getLength() > position.getOffset());
		}
		
		@Override
		public int compareTo(Region region) {
			int result = this.range.getFile().compareTo(region.range.getFile());
			if (result != 0)
				return result;
			
			result = this.range.getOffset() - region.range.getOffset();
			if (result != 0)
				return result;
			
			result = this.range.getLength() - region.range.getLength();
			return result;
		}
		
	}
	
	/**
	 * This class contains information about a position.
	 */
	public class PositionInfo {

		private ASTNode functionDeclaration;
		private ASTNode inLineHtml;
		private ASTNode scalar;
		private ASTNode echoPrintStatement;
		
		public PositionInfo(ASTNode functionDeclaration, ASTNode inLineHtml, ASTNode scalar, ASTNode echoPrintStatement) {
			this.functionDeclaration = functionDeclaration;
			this.inLineHtml = inLineHtml;
			this.scalar = scalar;
			this.echoPrintStatement = echoPrintStatement;
		}
		
		public ASTNode getFunctionDeclaration() {
			return functionDeclaration;
		}

		public ASTNode getInLineHtml() {
			return inLineHtml;
		}

		public ASTNode getScalar() {
			return scalar;
		}
		
		public ASTNode getEchoPrintStatement() {
			return echoPrintStatement;
		}
		
	}

}

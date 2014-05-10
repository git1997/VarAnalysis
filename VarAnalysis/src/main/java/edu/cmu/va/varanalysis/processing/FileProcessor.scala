package edu.cmu.va.varanalysis.processing

import org.eclipse.core.resources.IFile
import edu.cmu.va.varanalysis.model.SymExModel
import edu.iastate.symex.datamodel.nodes.DataNode
import edu.iastate.symex.errormodel.SymexErrorHandler
import edu.iastate.symex.errormodel.SymexException
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterToken
import java.io.File
import de.fosd.typechef.parser.html._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.TokenReader
import edu.iastate.symex.datamodel.nodes.DataNode
import edu.cmu.va.varanalysis.model.CallGraph
import edu.cmu.va.varanalysis.model.PositionRange
import edu.iastate.symex.run.RunSymexForFile

class FileProcessor {
    def process(ifile: IFile, reporter: SymexErrorHandler) {
        val file = ifile.getRawLocation().makeAbsolute().toFile();

        val model = executeSymbolically(reporter, file);
        SymExModel.getInstance().updateDModel(ifile, model);

        if (model == null) {
            reporter.fatalError(new SymexException("could not create D-Model", file, 0, 0));
            return ;
        }

        val vardom = parseVarDom(model, reporter);

        if (model == null) {
            reporter.fatalError(new SymexException("could not create VarDOM", file, 0, 0));
            return ;
        }

        SymExModel.getInstance().updateVarDom(ifile, vardom);
        
        
        val htmlCallGraph=getHTMLCallGraph(vardom)

        println(htmlCallGraph)
        
        SymExModel.getInstance().updateCallGraph(ifile, htmlCallGraph) 
    }

    def parseVarDom(model: DataNode, reporter: SymexErrorHandler): VarDom = {
        val tokens = lexDModel(model)
        val tagSequence = parseSAX(tokens, reporter)
        parseDOM(tagSequence, reporter)

    }

    def lexDModel(model: DataNode): TokenReader[CharacterToken, Object] = new DModelLexer().lex(model);

    def parseSAX(tokens: TokenReader[CharacterToken, Object], reporter: SymexErrorHandler): List[HElementToken] = {
        val p = new HTMLSAXParser
        val parseResult = p.phrase(p.HtmlSequence)(tokens, FeatureExprFactory.True)

        def getParseResult(parseResult: p.MultiParseResult[List[Opt[HElement]]], ctx: FeatureExpr): List[Opt[HElement]] = parseResult match {
            case p.Success(r, rest) =>
                if (!rest.atEnd)
                    reporter.error(
                        new SymexException("error: SAXParser not at end: " + rest,
                            new File(rest.first.getPosition.getFile),
                            rest.first.getPosition.getLine,
                            rest.first.getPosition.getColumn))
                r.map(_.and(ctx))
            case p.NoSuccess(msg, rest, _) =>
                reporter.error(
                    new SymexException(msg,
                        new File(rest.first.getPosition.getFile),
                        rest.first.getPosition.getLine,
                        rest.first.getPosition.getColumn))
                Nil
            case p.SplittedParseResult(f, a, b) => getParseResult(a, ctx and f) ++ getParseResult(b, ctx andNot f)
        }

        val helemlist = getParseResult(parseResult, FeatureExprFactory.True)

        helemlist.map(t => new HElementToken(t))
    }

    def parseDOM(tokens: List[HElementToken], reporter: SymexErrorHandler): VarDom = {
        val p2 = new HTMLDomParser
        val eofToken = new HElementToken(Opt(FeatureExprFactory.True, HText(List())))
        val tokenStream = new TokenReader[HElementToken, Null](tokens, 0, null, eofToken)

        val domResult = p2.phrase(p2.Document)(tokenStream, FeatureExprFactory.True)

        def getParseResult(parseResult: p2.MultiParseResult[List[Opt[DElement]]], ctx: FeatureExpr): List[Opt[DElement]] = parseResult match {
            case p2.Success(r, rest) =>
                if (!rest.atEnd)
                    reporter.error(
                        new SymexException("error: SAXParser not at end: " + rest,
                            new File(rest.first.getPosition.getFile),
                            rest.first.getPosition.getLine,
                            rest.first.getPosition.getColumn))
                r.map(_.and(ctx))
            case p2.NoSuccess(msg, rest, _) =>
                reporter.error(
                    new SymexException(msg,
                        new File(rest.first.getPosition.getFile),
                        rest.first.getPosition.getLine,
                        rest.first.getPosition.getColumn))
                Nil
            case p2.SplittedParseResult(f, a, b) => getParseResult(a, ctx and f) ++ getParseResult(b, ctx andNot f)
        }

        VarDom(getParseResult(domResult, FeatureExprFactory.True))
    }

    def executeSymbolically(reporter: SymexErrorHandler, file: File): DataNode =
        new RunSymexForFile(file).execute().getRoot();

    
    def getHTMLCallGraph(vardom:VarDom): CallGraph = {
        var callgraph= new CallGraph()
        
        def dstringToRange(v:DString): PositionRange = 
            new PositionRange(v.getFile.getOrElse("<unknown>"), v.getPositionFrom.getColumn, v.getPositionTo.getColumn)
        
        def getHTMLEdge(element: DElement, ctx: FeatureExpr):Unit = element match {
            case DNode(name, _, children, openTag, closingTag) =>
                callgraph.addEdge(dstringToRange(openTag.name), dstringToRange(closingTag.name), ctx)
                for (Opt(f,c)<-children)
                    getHTMLEdge(c, ctx and f)
            case DText(_) =>
                
        }
        
        for (Opt(f,c)<-vardom.children)
        	getHTMLEdge(c, f)
        
        callgraph
    }
    
}
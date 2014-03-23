package edu.cmu.va.varanalysis.processing

import org.eclipse.core.resources.IFile
import errormodel.SymExErrorHandler
import edu.cmu.va.varanalysis.model.SymExModel
import datamodel.nodes.DataNode
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterToken
import errormodel.SymExException
import java.io.File
import varanalysis.RunFile
import de.fosd.typechef.parser.html._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.TokenReader

class FileProcessor {
    def process(ifile: IFile, reporter: SymExErrorHandler) {
        val file = ifile.getRawLocation().makeAbsolute().toFile();

        val model = executeSymbolically(reporter, file);
        SymExModel.getInstance().updateDModel(ifile, model);

        if (model == null) {
            reporter.fatalError(new SymExException("could not create D-Model",
                file, 0));
            return ;
        }

        val vardom = parseVarDom(model, reporter);

        if (model == null) {
            reporter.fatalError(new SymExException("could not create VarDOM",
                file, 0));
            return ;
        }

        SymExModel.getInstance().updateVarDom(ifile, vardom);
    }

    def parseVarDom(model: DataNode, reporter: SymExErrorHandler): VarDom = {
        val tokens = lexDModel(model)
        val tagSequence = parseSAX(tokens, reporter)
        parseDOM(tagSequence, reporter)

    }

    def lexDModel(model: DataNode): TokenReader[CharacterToken, Object] = new DModelLexer().lex(model);

    def parseSAX(tokens: TokenReader[CharacterToken, Object], reporter: SymExErrorHandler): List[HElementToken] = {
        val p = new HTMLSAXParser
        val parseResult = p.phrase(p.HtmlSequence)(tokens, FeatureExprFactory.True)

        def getParseResult(parseResult: p.MultiParseResult[List[Opt[HElement]]], ctx: FeatureExpr): List[Opt[HElement]] = parseResult match {
            case p.Success(r, rest) =>
                if (!rest.atEnd)
                    reporter.error(
                        new SymExException("error: SAXParser not at end: " + rest,
                            new File(rest.first.getPosition.getFile),
                            rest.first.getPosition.getColumn))
                r.map(_.and(ctx))
            case p.NoSuccess(msg, rest, _) =>
                reporter.error(
                    new SymExException(msg,
                        new File(rest.first.getPosition.getFile),
                        rest.first.getPosition.getColumn))
                Nil
            case p.SplittedParseResult(f, a, b) => getParseResult(a, ctx and f) ++ getParseResult(b, ctx andNot f)
        }

        val helemlist = getParseResult(parseResult, FeatureExprFactory.True)

        helemlist.map(t => new HElementToken(t))
    }

    def parseDOM(tokens: List[HElementToken], reporter: SymExErrorHandler): VarDom = {
        val p2 = new HTMLDomParser
        val eofToken = new HElementToken(Opt(FeatureExprFactory.True, HText(List())))
        val tokenStream = new TokenReader[HElementToken, Null](tokens, 0, null, eofToken)

        val domResult = p2.phrase(p2.Document)(tokenStream, FeatureExprFactory.True)

        def getParseResult(parseResult: p2.MultiParseResult[List[Opt[DElement]]], ctx: FeatureExpr): List[Opt[DElement]] = parseResult match {
            case p2.Success(r, rest) =>
                if (!rest.atEnd)
                    reporter.error(
                        new SymExException("error: SAXParser not at end: " + rest,
                            new File(rest.first.getPosition.getFile),
                            rest.first.getPosition.getColumn))
                r.map(_.and(ctx))
            case p2.NoSuccess(msg, rest, _) =>
                reporter.error(
                    new SymExException(msg,
                        new File(rest.first.getPosition.getFile),
                        rest.first.getPosition.getColumn))
                Nil
            case p2.SplittedParseResult(f, a, b) => getParseResult(a, ctx and f) ++ getParseResult(b, ctx andNot f)
        }

        VarDom(getParseResult(domResult, FeatureExprFactory.True))
    }

    def executeSymbolically(reporter: SymExErrorHandler, file: File): DataNode =
        new RunFile(file, new File(".")).run(reporter);

}
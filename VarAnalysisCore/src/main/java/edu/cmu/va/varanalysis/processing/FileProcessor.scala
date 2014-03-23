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

class FileProcessor {
  def process(ifile: IFile, reporter: SymExErrorHandler) {
    val file = ifile.getRawLocation().makeAbsolute().toFile();

    val model = executeSymbolically(reporter, file);
    SymExModel.getInstance().updateModel(ifile, model);

    if (model == null) {
      reporter.fatalError(new SymExException("could not create D-Model",
        file, 0));
      return ;
    }

    parseVarDom(model, reporter);

  }

  def parseVarDom(model: DataNode, reporter: SymExErrorHandler) {
    val tokens = lexDModel(model);

    assert(tokens != null)

    val tagSequence = parseSAX(tokens, reporter)

    //    // stage 2: DOM parser
    //
    //    var domTokens = List[HElementToken]()
    //    tagSequence match {
    //        case p.Success(r, rest) =>
    //            domTokens = r.map(t=>new HElementToken(t))
    //            if (!rest.atEnd) println("error: SAXParser not at end: "+rest)
    //        case x => println("parsing problem: "+x)
    //    }
    //
    //    println(domTokens.mkString("\n"))
    //
    //    println("\n\n")
    //
    //    val p2 = new HTMLDomParser
    //
    //    val tokenStream = new TokenReader[HElementToken, Null](domTokens, 0, null, new HElementToken(Opt(FeatureExprFactory.True,HText(List()))))
    //
    //
    //    val dom = p2.phrase(p2.Element)(tokenStream,FeatureExprFactory.True)
    //
    //    println(dom)

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

  def executeSymbolically(reporter: SymExErrorHandler, file: File): DataNode =
    new RunFile(file, new File(".")).run(reporter);

}
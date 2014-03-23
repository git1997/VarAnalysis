package edu.cmu.va.varanalysis.processing

import datamodel.nodes.DataNode
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterToken
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.common.JPosition
import datamodel.nodes.LiteralNode
import de.fosd.typechef.featureexpr.FeatureExpr
import datamodel.nodes.SelectNode
import datamodel.nodes.RepeatNode
import datamodel.nodes.ConcatNode
import datamodel.nodes.LiteralNode
import datamodel.nodes.RepeatNode
import datamodel.nodes.SelectNode
import datamodel.nodes.ConcatNode
import datamodel.nodes.DataNode
import datamodel.nodes.LiteralNode
import scala.collection.JavaConversions.asScalaBuffer

class DModelLexer {
  import scala.collection.JavaConversions._

  val noPosition = new JPosition("", -1, -1)
  val eof = new CharacterToken(-1, FeatureExprFactory.False, noPosition)

  def lex(model: DataNode): TokenReader[CharacterToken, Object] = {

    val tokens = lexNode(model, FeatureExprFactory.True)

    new TokenReader(tokens.reverse, 0, null, eof)
  }

  def lexNode(node: DataNode, ctx: FeatureExpr): List[CharacterToken] = {
    if (node.isInstanceOf[LiteralNode]) lexLiteralNode(node.asInstanceOf[LiteralNode], ctx)
    else if (node.isInstanceOf[ConcatNode]) lexConcatNode(node.asInstanceOf[ConcatNode], ctx)
    else if (node.isInstanceOf[SelectNode]) lexSelectNode(node.asInstanceOf[SelectNode], ctx)
    else if (node.isInstanceOf[RepeatNode]) lexRepeatNode(node.asInstanceOf[RepeatNode], ctx)
    else Nil
  }

  def lexLiteralNode(node: LiteralNode, ctx: FeatureExpr): List[CharacterToken] = {
    val str= node.getUnescapedStringValue()
    val loc = node.getLocation().getLocationAtOffset(0)
    val file = loc.getFilePath().getName()
    
    var p=loc.getPosition()
    var result:List[CharacterToken]=Nil
    for (c<-str) {
      result = new CharacterToken(c, ctx, new JPosition(file, loc.getLine(), p)) :: result
      p += 1
    }
    
    result
  }

  def lexSelectNode(node: SelectNode, ctx: FeatureExpr): List[CharacterToken] = {
    val fexpr = FeatureExprFactory.createDefinedExternal(node.getSymbolicValue())
    var result: List[CharacterToken] = Nil
    if ((ctx and fexpr).isSatisfiable && node.getNodeInTrueBranch() != null)
      result = lexNode(node.getNodeInTrueBranch(), ctx and fexpr) ++ result
    if ((ctx andNot fexpr).isSatisfiable && node.getNodeInFalseBranch() != null)
      result = lexNode(node.getNodeInFalseBranch(), ctx andNot fexpr) ++ result
    result
  }

  def lexRepeatNode(node: RepeatNode, ctx: FeatureExpr): List[CharacterToken] = lexNode(node.getChildNode(), ctx)
  def lexConcatNode(node: ConcatNode, ctx: FeatureExpr): List[CharacterToken] =
    node.getChildNodes().map(lexNode(_, ctx)).foldLeft[List[CharacterToken]](Nil)((b, a) => a ++ b)
}
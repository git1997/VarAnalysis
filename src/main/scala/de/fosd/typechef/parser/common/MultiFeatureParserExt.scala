package de.fosd.typechef.parser.common

import de.fosd.typechef.parser.MultiFeatureParser
import de.fosd.typechef.parser.~

trait MultiFeatureParserExt extends MultiFeatureParser {
  
  
  /**
   * repetitions 0..n with separator
   *
   * for the pattern
   * [p ~ (separator ~ p)*]
   */
  def repSepPlain[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
    opt(p ~ repPlain(separator ~> p)) ^^ {
      case Some(first ~ rest) => first :: rest
      case None => List()
    }

  def repSepPlain1[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
    p ~ repPlain(separator ~> p) ^^ {
      case first ~ rest => first :: rest
    }
  
   def rep1Plain[T](p: => MultiParser[T]): MultiParser[List[T]] = p ~ repPlain(p) ^^ { case h~r => h::r }

}
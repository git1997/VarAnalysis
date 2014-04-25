package edu.iastate.varis.core

import de.fosd.typechef.parser.html._
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.common.CharacterToken

/**
 * @author HUNG
 */
object ScalaToJava {
  
	def getChildrenOfVarDom(varDom: VarDom) = varDom.children;
	
	def getFileOfDString(dString: DString) = dString.getPositionFrom.getFile
	
	def getPositionOfDString(dString: DString) = dString.getPositionFrom.getColumn
 	
	def scalaListToJavaList(scalaList: List[Opt[DElement]]): java.util.ArrayList[Opt[DElement]] = {
	  val javaList = new java.util.ArrayList[Opt[DElement]]()
	  for (child <- scalaList)
		  javaList.add(child)
	  return javaList
	}
	
	def scalaListToJavaList2(scalaList: List[Opt[CharacterToken]]): java.util.ArrayList[Opt[CharacterToken]] = {
	  val javaList = new java.util.ArrayList[Opt[CharacterToken]]()
	  for (child <- scalaList)
		  javaList.add(child)
	  return javaList
	}
	
	def scalaListToJavaList3(scalaList: List[Opt[HAttribute]]): java.util.ArrayList[Opt[HAttribute]] = {
	  val javaList = new java.util.ArrayList[Opt[HAttribute]]()
	  for (child <- scalaList)
		  javaList.add(child)
	  return javaList
	}
	
	def getOptionValue(o: Option[String]) = o.getOrElse("(empty)")
	
}
package org.uqbar.tadep.macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object E04_Macros {

	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Ejemplo fácil
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

	def id(n: Int): Int = macro id_impl

	def id_impl(c: Context)(n: c.Expr[Int]): c.Expr[Int] = {
		n
	}

	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Ejemplo con Quasiquotes
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	def assert(contidion: Boolean, msg: String): Unit = macro assert_impl
	
	def assert_impl(c: Context)(contidion: c.Expr[Boolean], msg: c.Expr[String]) = {
	  import c.universe._
	  
	  val q"assert ($condition, $msg)" = c.macroApplication
	  q"if (!$condition) raise($msg)"
	}
	
	
	def debug(code: => Unit): Unit = macro debug_impl

	def debug_impl(c: Context)(code: c.Tree) = {
		import c.universe._

		val q"..$sentences" = code

		val loggedSentences = (sentences :\ List[c.Tree]()){
			case (sentence, acum) =>
				val msg = "executing " + showCode(sentence)
				val printSentence = q"println($msg)"

				printSentence :: sentence :: acum
		}

		q"..$loggedSentences"
	}

	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Ejemplo con validación
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

	case class Email(id: String, domain: String)

	def email(str: String): Email = macro email_impl
	def email_impl(c: Context)(str: c.Expr[String]) = {
		import c.universe._

		val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
		
		str match {
			case Expr(Literal(Constant(emailFormat(id, domain)))) =>	q"""Email($id,$domain)"""
			case _ => c.abort(c.enclosingPosition, "Invalid mail!!!")
		}
	}
	
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════
	// Poniendo todo junto
	//═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════

//	case class Email(id: String, domain: String)
//
//	implicit class EmailStringContext(strCtx: StringContext) {
//		def email(arguments: Any*): Email = macro email_impl
//	}
//	
//	def email_impl(c: Context)(arguments: c.Expr[Any]*) = {
//		import c.universe._
//
//		val emailFormat = """(\w{4,})@([\w\.]+.com)""".r
//		
//		c.prefix.tree match {
//      case Apply(_, List(Apply(_, rawParts))) =>
//      	
//      	val parts = rawParts map { case Literal(Constant(const: String)) => const }
//      	val args = arguments map { case Literal(Constant(const: String)) => const }
//      	val mail = ("" /: parts.zipAll(args, "", "")) {	case (acum, (part, arg)) => acum + part + arg	}
//
//      	mail match {
//      		case emailFormat(id,domain) => q"Email($id,$domain)"
//      		case _ => c.abort(c.enclosingPosition, "Invalid mail!!!")
//      	}
//      	
//			case _ => c.abort(c.enclosingPosition, "Invalid mail!!!")
//		}
//	}
}

object USO {
	import E04_Macros._

}
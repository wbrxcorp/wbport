package modules.wbport

import java.security.MessageDigest
import scalikejdbc.{DB,NamedDB}

object Module extends modules.Module with scalikejdbc.SQLInterpolation {
  //override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {}
  override def dependsOn:Seq[String] = Seq("database")

  def getSha256(str:String):String = {
    val buf = new StringBuffer()
    val md = MessageDigest.getInstance("SHA-256")
    md.update(str.getBytes())
    md.digest().foreach { b =>
      buf.append("%02x".format(b))
    }
    buf.toString
  }

  def encryptPassword(password:String):String = {
    val salt = new scala.util.Random(new java.security.SecureRandom()).alphanumeric.take(5).mkString
    "%s$%s".format(salt, getSha256("%s%s".format(salt, password)))
  }

  def setPassword(email:String, password:String)(implicit session:scalikejdbc.DBSession):Int = {
    sql"update users set password=${encryptPassword(password)} where email=${email}".update.apply
  }

  def addUser(email:String, password:String)(implicit session:scalikejdbc.DBSession):Int = {
    sql"insert into users(email,password) values(${email},${encryptPassword(password)})".update.apply
  }
}

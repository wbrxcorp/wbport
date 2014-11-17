package com.wbport

import java.security.MessageDigest
import java.sql.SQLException
import com.walbrix.spring.ScalikeJdbcSupport
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import scalikejdbc.{WrappedResultSet, _}

case class User(id:Int, email:String, admin:Boolean, passwordPresent:Boolean)
case class Server(id:Int, fqdn:String)
case class Domain(domain:String)

/**
 * Created by shimarin on 14/11/12.
 */
@Component
trait DAO extends ScalikeJdbcSupport {

  def getUser[A](sql:SQL[A, NoExtractor]):Option[User] = {
    single(sql.map { row =>
      User(row.int("id"),row.string("email"), row.boolean("admin_user"), row.stringOpt("password").nonEmpty)
    })
  }

  def getUser(userId:Int):Option[User] =
    getUser(sql"select * from users where id=${userId}")

  def getUser(username:String):Option[User] =
    getUser(sql"select * from users where email=${username}")

  def getUserHasPassword(username:String):Option[User] =
    getUser(sql"select * from users where email=${username} and password is not null")

  def getUserByAuthToken(authToken:String):Option[User] =
    getUser(sql"select * from users where auth_token=${authToken}")

  def getUserHasNoPasswordByAuthToken(authToken:String):Option[User] =
    getUser(sql"select * from users where auth_token=${authToken} and password is null")

  def checkPassword(username:String,password:String):Option[(Int,String,Boolean)] = {
    val (userId,encrypted, authToken,admin) = single(sql"select id,password,auth_token,admin_user from users where email=${username}".map(row=>(row.int(1),row.string(2),row.string(3),row.boolean(4)))).getOrElse(return None)
    comparePassword(encrypted, password) match {
      case true => Some((userId,authToken,admin))
      case false => None
    }
  }

  private def getSha256(str:String):String = {
    val buf = new StringBuffer()
    val md = MessageDigest.getInstance("SHA-256")
    md.update(str.getBytes())
    md.digest().foreach { b =>
      buf.append("%02x".format(b))
    }
    buf.toString
  }

  def comparePassword(encrypted:String, password:String):Boolean = {
    encrypted.split('$') match {
      case x if x.length == 2 => x(1) == getSha256("%s%s".format(x(0), password))
      case _ => false
    }
  }

  def encryptPassword(password:String):String = {
    val salt = new scala.util.Random(new java.security.SecureRandom()).alphanumeric.take(5).mkString
    "%s$%s".format(salt, getSha256("%s%s".format(salt, password)))
  }

  def generateAuthToken(email:String):String =
    "%s%s".format(getSha256(email), new scala.util.Random(new java.security.SecureRandom()).alphanumeric.take(16).mkString)

  def resetAuthToken(userId:Int):Option[String] = {
    string(sql"select email from users where id=${userId}").map { email =>
      val authToken = generateAuthToken(email)
      update(sql"update users set auth_token=${authToken},auth_token_expires_at=DATEADD('MONTH', 6, now()) where id=${userId}")
      authToken
    }
  }

  def getServers(userId:Int):Seq[Server] =
    list(sql"select * from servers where user_id=${userId}".map(row=>Server(row.int("id"),row.string("fqdn"))))

  def createServer(userId:Int, fqdn:String):Option[Int] = {
    try {
      update(sql"insert into servers(fqdn,user_id) values(${fqdn.toLowerCase},${userId})")
      int(sql"select last_insert_id()")
    }
    catch {
      case e:DuplicateKeyException => None
    }
  }

  def deleteServer(userId:Int, fqdn:String):Boolean =
    update(sql"delete from servers where user_id=${userId} and fqdn=${fqdn}") > 0

  def getDomains(userId:Int):Seq[String] =
    list(sql"select * from domains where user_id=${userId}".map(_.string("domain_name")))

  def createMailLog(recipient:String, subject:Option[String] = None, success:Boolean = true, errorMessage:Option[String] = None):Unit =
    update(sql"insert into mail_log(recipient,subject,success,error_message) values(${recipient},${subject},${success},${errorMessage})")

  def getSystemConfig(configKey:String):Option[String] =
    string(sql"select config_value from system_config where config_key=${configKey}")

  def getSystemEmailFrom():String = getSystemConfig("system_email_from").getOrElse("support@wbport.com")
}

package com.wbport

import java.security.MessageDigest
import java.sql.SQLException

import com.walbrix.spring.scalikejdbc.ScalikeJdbcSupport
import org.springframework.stereotype.Component
import scalikejdbc.{WrappedResultSet, _}

case class User(id:Int, email:String)
case class Server(id:Int, fqdn:String)
case class Domain(domainName:String)

/**
 * Created by shimarin on 14/11/12.
 */
@Component
trait DAO extends ScalikeJdbcSupport {
  private def row2user(row:WrappedResultSet):User = {
    User(row.int("id"),row.string("email"))
  }

  def getUser(userId:Int):Option[User] = {
    sql"select * from users where id=${userId}".map(row2user(_)).single().apply()
  }

  def getUser(username:String):Option[User] = {
    sql"select * from users where email=${username}".map(row2user(_)).single().apply()
  }

  def getUserHasPassword(username:String):Option[User] = {
    sql"select * from users where email=${username} and password is not null".map(row2user(_)).single().apply()
  }

  def getUserByAuthToken(authToken:String):Option[User] = {
    sql"select * from users where auth_token=${authToken}".map(row2user(_)).single().apply()
  }

  def getUserHasNoPasswordByAuthToken(authToken:String):Option[User] = {
    sql"select * from users where auth_token=${authToken} and password is null".map(row2user(_)).single().apply()
  }

  def checkPassword(username:String,password:String):Option[(Int,String)] = {
    val (userId,encrypted, authToken) = sql"select id,password,auth_token from users where email=${username}".map(row=>(row.int(1),row.string(2),row.string(3))).single().apply().getOrElse(return None)
    comparePassword(encrypted, password) match {
      case true => Some((userId,authToken))
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

  def generateAuthToken(email:String):String = {
    "%s%s".format(getSha256(email), new scala.util.Random(new java.security.SecureRandom()).alphanumeric.take(16).mkString)
  }

  def resetAuthToken(userId:Int):Option[String] = {
    sql"select email from users where id=${userId}".map(_.string(1)).single().apply().map { email =>
      val authToken = generateAuthToken(email)
      sql"update users set auth_token=${authToken},auth_token_expires_at=DATEADD('MONTH', 6, now()) where id=${userId}".update().apply()
      authToken
    }
  }

  def row2server(row:WrappedResultSet):Server = {
    Server(row.int("id"),row.string("fqdn"))
  }

  def getServers(userId:Int):Seq[Server] = {
    sql"select * from servers where user_id=${userId}".map(row2server(_)).list().apply()
  }

  def createServer(userId:Int, fqdn:String):Option[Int] = {
    try {
      sql"insert into servers(fqdn,user_id) values(${fqdn},${userId})".update().apply()
      sql"select last_insert_id()".map(_.int(1)).single().apply()
    }
    catch {
      case e:SQLException if e.getErrorCode == 23505/*DUPLICATE_KEY_1*/ =>
        None
    }
  }

  def deleteServer(userId:Int, fqdn:String):Boolean = {
    sql"delete from servers where user_id=${userId} and fqdn=${fqdn}".update().apply() > 0
  }

  def getDomains(userId:Int):Seq[Domain] = {
    sql"select * from domains where user_id=${userId}".map{ row => Domain(row.string("domain_name"))}.list().apply()
  }
}

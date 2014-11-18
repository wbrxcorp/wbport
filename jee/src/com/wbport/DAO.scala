package com.wbport

import java.security.MessageDigest
import java.sql.SQLException
import com.walbrix.spring.ScalikeJdbcSupport
import com.walbrix.spring.mvc.CRUD
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import scalikejdbc.{WrappedResultSet, _}

case class User(id:Int, email:String, admin:Boolean, passwordPresent:Boolean)
case class Server(id:Int, fqdn:String)
case class Domain(domain:String)

/**
 * Created by shimarin on 14/11/12.
 */

trait UserDAO extends ScalikeJdbcSupport {
  private def getUser[A](sql:SQL[A, NoExtractor]):Option[User] = {
    single(sql.map { row =>
      User(row.int("id"),row.string("email"), row.boolean("admin_user"), row.stringOpt("password").nonEmpty)
    })
  }

  def getUser(userId:Int):Option[User] =
    getUser(sql"select * from users where id=${userId}")

  def getUserByAuthToken(authToken:String):Option[User] =
    getUser(sql"select * from users where auth_token=${authToken}")

  def getUser(username:String):Option[User] =
    getUser(sql"select * from users where email=${username}")

  def getUserHasPassword(username:String):Option[User] =
    getUser(sql"select * from users where email=${username} and password is not null")

  def getUserHasNoPasswordByAuthToken(authToken:String):Option[User] =
    getUser(sql"select * from users where auth_token=${authToken} and password is null")
}

trait LogDAO extends ScalikeJdbcSupport {
  def createMailLog(recipient:String, subject:Option[String] = None, success:Boolean = true, errorMessage:Option[String] = None):Unit =
    update(sql"insert into mail_log(recipient,subject,success,error_message) values(${recipient},${subject},${success},${errorMessage})")
}

trait SystemConfig extends ScalikeJdbcSupport {
  def getSystemConfig(configKey:String):Option[String] =
    string(sql"select config_value from system_config where config_key=${configKey}")

  def getSystemEmailFrom():String = getSystemConfig("system_email_from").getOrElse("support@wbport.com")
}

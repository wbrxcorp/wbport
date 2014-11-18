package com.wbport

import com.walbrix.spring.ScalikeJdbcSupport
import scalikejdbc._

/**
 * Created by shimarin on 14/11/14.
 */
trait Authentication extends com.walbrix.spring.mvc.Authentication[User, Int] with UserDAO {
  override def getIdFromUser(user:User):Int = user.id

  def checkPassword(username:String,password:String):Option[(Int,String)] = {
    val (userId,encrypted, authToken) = single(sql"select id,password,auth_token from users where email=${username}".map(row=>(row.int(1),row.string(2),row.string(3)))).getOrElse(return None)
    com.walbrix.comparePassword(encrypted, password) match {
      case true => Some((userId,authToken))
      case false => None
    }
  }

  def resetAuthToken(userId:Int):Option[String] = {
    string(sql"select email from users where id=${userId}").map { email =>
      val authToken = com.walbrix.generateAuthToken(email)
      update(sql"update users set auth_token=${authToken},auth_token_expires_at=DATEADD('MONTH', 6, now()) where id=${userId}")
      authToken
    }
  }
}

class AuthenticationBean extends com.walbrix.spring.mvc.AuthenticationBean[User,Int] with Authentication {
}

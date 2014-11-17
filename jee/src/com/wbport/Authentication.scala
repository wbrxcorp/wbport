package com.wbport

/**
 * Created by shimarin on 14/11/14.
 */
trait Authentication extends DAO with com.walbrix.spring.mvc.Authentication[User, Int] {
  override def getIdFromUser(user:User):Int = user.id
}

class AuthenticationBean extends com.walbrix.spring.mvc.AuthenticationBean[User,Int] with Authentication {
}

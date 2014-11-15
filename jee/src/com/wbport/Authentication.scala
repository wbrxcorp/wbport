package com.wbport

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, Cookie, HttpSession}

import com.walbrix.spring.mvc.HttpErrorStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.request.{ServletRequestAttributes, RequestContextHolder}

/**
 * Created by shimarin on 14/11/14.
 */
trait Authentication extends DAO with HttpErrorStatus {
  val authTokenCookieName = "auth_token"
  val userIdSessionAttrKey = "user_id"
  val userRequestAttrKey = "user"

  @Autowired private var request:HttpServletRequest = _
  @Autowired private var response:HttpServletResponse = _

  private def sendCredentials(userId:Int, authToken:String, admin:Boolean):Unit = {
    val session = request.getSession()
    session.invalidate()
    val cookie: Cookie = new Cookie(authTokenCookieName, authToken)
    cookie.setPath("/")
    cookie.setMaxAge(60*60*24*180)
    response.addCookie(cookie)
    val newSession = request.getSession()
    newSession.setAttribute(userIdSessionAttrKey, userId)
  }

  def login(username:String, password:String):Boolean = {
    checkPassword(username, password).map { case (userId,authToken,admin) =>
      sendCredentials(userId, authToken, admin)
      true
    }.getOrElse(false)
  }

  def loginWithFreshAuthToken(userId:Int):Boolean = {
    resetAuthToken(userId).map { authToken =>
      sendCredentials(userId, authToken,false/*サインアップしたばかりの人はadminではないだろう*/)
      true
    }.getOrElse(false)
  }

  def logout():Boolean = {
    resetAuthToken(getUser().id)
    request.getSession().invalidate()
    true
  }

  def getAuthToken():Option[String] = {
    Option(request.getCookies).flatMap(_.find(_.getName.equals(authTokenCookieName)).map(_.getValue))
  }

  def getUserOpt():Option[User] = {
    Option(request.getAttribute(userRequestAttrKey).asInstanceOf[User]).foreach { user =>
      return Some(user)
    }
    val session = request.getSession()
    Option(session.getAttribute(userIdSessionAttrKey).asInstanceOf[Integer]).foreach { userId =>
      getUser(userId).foreach { user =>
        request.setAttribute(userRequestAttrKey, user)
        return Some(user)
      }
    }
    getAuthToken.foreach { authToken =>
      getUserByAuthToken(authToken).foreach { user =>
        request.setAttribute(userRequestAttrKey, user)
        session.setAttribute(userIdSessionAttrKey, user.id  )
        return Some(user)
      }
    }
    None
  }

  def getUser():User = getUserOpt().getOrElse(raiseForbidden)
}

class AuthenticationBean extends Authentication {
  override def equals(obj:Any):Boolean = {
    obj match {
      case x:String if x.equals("auth") => getUserOpt().map(_.isInstanceOf[User]).getOrElse(false)
      case x => super.equals(x)
    }
  }
}

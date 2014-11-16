package com.walbrix.spring

import javax.servlet.http.{Cookie, HttpSession, HttpServletResponse, HttpServletRequest}

import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by shimarin on 14/11/15.
 */
trait HttpContextSupport {
  @Autowired private var request:HttpServletRequest = _
  @Autowired private var response:HttpServletResponse = _

  def getSession(createNew:Boolean = true):HttpSession = request.getSession(createNew)
  def addCookie(cookie:Cookie):Unit = response.addCookie(cookie)
  def getAttribute(name:String):AnyRef = request.getAttribute(name)
  def setAttribute(name:String,value:AnyRef):Unit = request.setAttribute(name, value)
  def getCookies():Array[Cookie] = request.getCookies

  def getContextURL():String = {
    val buf = new StringBuilder
    val scheme = request.isSecure match {
      case true => ("https", 443)
      case false => ("http", 80)
    }
    buf.append(scheme._1)
    buf.append("://")
    buf.append(request.getServerName)
    buf.append(
      request.getServerPort match {
        case port if port != scheme._2 => ":%d".format(port)
        case _ => ""
      }
    )
    buf.append(request.getContextPath)
    buf.toString()
  }
}

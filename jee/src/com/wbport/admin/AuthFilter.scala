package com.wbport.admin

import javax.servlet._
import javax.servlet.http.HttpServletResponse

import com.wbport.Authentication

/**
 * Created by shimarin on 14/11/15.
 */
class AuthFilter extends Filter with Authentication {
  private var enabled:Boolean = true
  def setEnabled(enabled:Boolean):Unit = this.enabled = enabled

  def isAdminUser():Boolean = {
    getAuthToken().flatMap(getUserByAuthToken(_)).map(_.admin).getOrElse(false)
  }

  override def doFilter(request:ServletRequest,response:ServletResponse,chain:FilterChain):Unit = {
    if (!enabled || isAdminUser()) {
      chain.doFilter(request, response)
    } else {
      response.asInstanceOf[HttpServletResponse].sendError(HttpServletResponse.SC_FORBIDDEN, "Access restricted")
    }
  }
  override def init(filterConfig: FilterConfig): Unit = {}

  override def destroy(): Unit = {}
}

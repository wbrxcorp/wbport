package com.wbport.admin

import javax.servlet._
import javax.servlet.http.HttpServletResponse

import com.wbport.Authentication

/**
 * Created by shimarin on 14/11/15.
 */
class AuthFilter extends Filter with Authentication {
  override def doFilter(request:ServletRequest,response:ServletResponse,chain:FilterChain):Unit = {
    getAuthToken().flatMap(getUserByAuthToken(_)).filter(_.admin).map { user =>
      chain.doFilter(request, response)
    }.getOrElse(response.asInstanceOf[HttpServletResponse].sendError(HttpServletResponse.SC_FORBIDDEN, "Access restricted"))
  }
  override def init(filterConfig: FilterConfig): Unit = {}

  override def destroy(): Unit = {}
}

package com.wbport.api

import com.walbrix.spring.ScalikeJdbcSupport
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.{RequestParam, ResponseBody, RequestMethod, RequestMapping}

/**
 * Created by shimarin on 14/11/14.
 */
@Controller
@Transactional
@RequestMapping(Array(""))
class RequestHandler extends ScalikeJdbcSupport {
  @RequestMapping(value=Array("vpn-auth"), method = Array(RequestMethod.GET))
  @ResponseBody
  def vpnAuth(@RequestParam fqdn:String, @RequestParam password:String):(Boolean, Option[String]) = {
    val rst = string(sql"select password from users,servers where servers.user_id=users.id and fqdn=${fqdn}").map { encrypted =>
      com.walbrix.comparePassword(encrypted, password)
    }.getOrElse(false)
    (rst, None)
  }
}

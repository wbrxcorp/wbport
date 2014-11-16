package com.wbport.api

import com.walbrix.spring.ScalikeJdbcSupport
import com.wbport.DAO
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.{RequestParam, ResponseBody, RequestMethod, RequestMapping}
import scalikejdbc._

/**
 * Created by shimarin on 14/11/14.
 */
@Controller
@Transactional
@RequestMapping(Array(""))
class RequestHandler extends ScalikeJdbcSupport with DAO {
  @RequestMapping(value=Array("vpn-auth"), method = Array(RequestMethod.GET))
  @ResponseBody
  def vpnAuth(@RequestParam fqdn:String, @RequestParam password:String):(Boolean, Option[String]) = {
    val rst = string(sql"select password from users,servers where servers.user_id=users.id and fqdn=${fqdn}").map { encrypted =>
      comparePassword(encrypted, password)
    }.getOrElse(false)
    (rst, None)
  }
}

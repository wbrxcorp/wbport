package com.wbport.admin

import com.walbrix.spring.scalikejdbc.ScalikeJdbcSupport
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.{RequestBody, ResponseBody, RequestMethod, RequestMapping}
import scalikejdbc._

/**
 * Created by shimarin on 14/11/14.
 */
@Controller
@Transactional
@RequestMapping(Array(""))
class RequestHandler extends ScalikeJdbcSupport {
  val defaultDumpFile = "/tmp/wbport-dump.sql"

  @RequestMapping(value=Array("dump"), method = Array(RequestMethod.POST))
  @ResponseBody
  def dump(@RequestBody json:Map[String,AnyRef]):(Boolean, Option[String]) = {
    val dumpFile = json.get("file").getOrElse(defaultDumpFile)
    val rst = sql"script to ${dumpFile}".execute().apply()
    (rst, None)
  }

  @RequestMapping(value=Array("dump"), method = Array(RequestMethod.GET))
  @ResponseBody
  def dump():(Boolean, Option[String]) = {
    val dumpFile = defaultDumpFile
    val rst = sql"script to ${dumpFile}".execute().apply()
    (rst, None)
  }

}

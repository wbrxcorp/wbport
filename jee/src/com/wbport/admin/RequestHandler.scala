package com.wbport.admin

import com.walbrix.spring.EmailSupport
import com.walbrix.spring.scalikejdbc.ScalikeJdbcSupport
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.{RequestBody, ResponseBody, RequestMethod, RequestMapping}
import scalikejdbc._

/**
 * Created by shimarin on 14/11/14.
 */

case class Recipient(
  email:String,
  variables:Option[Map[String,String]]
)

case class BulkMail(
  subject:String,
  body:String,
  from:String,
  fromName:Option[String],
  recipients:Seq[Recipient]
)

case class Result(success:Boolean, info:Option[Any] = None)

@Controller
@Transactional
@RequestMapping(Array(""))
class RequestHandler extends ScalikeJdbcSupport with EmailSupport {
  val defaultDumpFile = "/tmp/wbport-dump.sql"

  @RequestMapping(value=Array("dump"), method = Array(RequestMethod.POST))
  @ResponseBody
  def dump(@RequestBody json:Map[String,AnyRef]):(Boolean, Option[String]) = {
    val dumpFile = json.get("file").getOrElse(defaultDumpFile)
    val rst = db(implicit session=>sql"script to ${dumpFile}".execute().apply())
    (rst, None)
  }

  @RequestMapping(value=Array("dump"), method = Array(RequestMethod.GET))
  @ResponseBody
  def dump():(Boolean, Option[String]) = {
    val dumpFile = defaultDumpFile
    val rst = db(implicit session=>sql"script to ${dumpFile}".execute().apply())
    (rst, None)
  }

  private def applyVariables(template:String, variables:Map[String,String]):String = {
    "\\$[a-zA-Z0-9_]+".r.replaceAllIn(template, { matched =>
      val original = matched.group(0)
      variables.get(original).getOrElse("\\" + original)
    })
  }

  @RequestMapping(value=Array("bulk-mail"), method=Array(RequestMethod.POST))
  @ResponseBody
  def bulkMail(@RequestBody json:BulkMail):Result = {
    val sender = createMailSender()
    json.recipients.foreach { recipient =>
      val message = sender.createJisMailMessage
      json.fromName match {
        case Some(fromName) => message.setFrom(json.from, fromName)
        case _ => message.setFrom(json.from)
      }
      message.setTo(recipient.email)
      val variables = recipient.variables.getOrElse(Map())
      message.setSubject(applyVariables(json.subject, variables))
      message.setText(applyVariables(json.body, variables))
      try {
        sender.send(message)
      }
      catch {
        case e:Exception =>
          // log
      }
    }

    Result(true)
  }

}

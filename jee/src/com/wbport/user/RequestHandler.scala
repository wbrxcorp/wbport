package com.wbport.user

import com.walbrix.spring.EmailSupport
import com.wbport.{Server, Domain, Authentication, DAO}
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation._
import scalikejdbc._

case class Result(success:Boolean, info:Option[Any] = None)
case class Auth(email:String,password:String)

/**
 * Created by shimarin on 14/11/12.
 */
@Controller
@Transactional
@RequestMapping(Array(""))
class RequestHandler extends DAO with Authentication with EmailSupport {
  @RequestMapping(value=Array("info"), method = Array(RequestMethod.GET))
  @ResponseBody
  def info():Map[String,Option[String]] = {
    Map("email"->getUserOpt().map(_.email))
  }

  @RequestMapping(value=Array("login"), method = Array(RequestMethod.POST),consumes=Array("application/json"))
  @ResponseBody
  def login(@RequestBody auth:Auth):Result = {
    Result(login(auth.email, auth.password))
  }

  @RequestMapping(value=Array("logout"), method = Array(RequestMethod.POST))
  @ResponseBody
  def logout_():Result = {
    Result(logout())
  }

  @RequestMapping(value=Array("signup"), method = Array(RequestMethod.POST),consumes=Array("application/json"))
  @ResponseBody
  def signup(@RequestBody json:Map[String,AnyRef]):Result = {
    val email = json("email").asInstanceOf[String]
    getUserHasPassword(email) match {
      case Some(user) => Result(false, Some("ALREADYEXISTS"))
      case None =>
        val authToken = generateAuthToken(email)
        val rst = db(implicit session=>sql"merge into users(email,auth_token,auth_token_expires_at) key(email) values(${email},${authToken},DATEADD('DAY', 1, now()))".update().apply())
        val sender = createMailSender()
        val message = sender.createJisMailMessage
        message.setFrom("support@wbport.com")
        message.setSubject("とうろくかくにそ")
        message.setTo(email)
        message.setText("http://hoge/user/confirm.html?k=%s".format(authToken))
        sender.send(message)
        Result(rst > 0)
    }
  }

  @RequestMapping(value=Array("confirm/{authToken}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def confirm(@PathVariable authToken:String):Map[String,Option[AnyRef]] = {
    getUserHasNoPasswordByAuthToken(authToken).map { user =>
      Map("email"->Some(user.email))
    }.getOrElse(Map("email"->None))
  }

  @RequestMapping(value=Array("confirm/{authToken}"), method = Array(RequestMethod.POST),consumes=Array("application/json"))
  @ResponseBody
  def doConfirm(@PathVariable authToken:String):Result = {
    val rst = getUserHasNoPasswordByAuthToken(authToken).map { user =>
      loginWithFreshAuthToken(user.id)
    }.getOrElse(false)
    Result(rst)
  }

  @RequestMapping(value=Array("server"), method = Array(RequestMethod.GET))
  @ResponseBody
  def server():Seq[Server] = {
    getServers(getUser().id)
  }

  @RequestMapping(value=Array("server"), method = Array(RequestMethod.POST),consumes=Array("application/json"))
  @ResponseBody
  def server(@RequestBody json:Map[String,AnyRef]):Result = {
    val rst = createServer(getUser().id, json("fqdn").asInstanceOf[String])
    Result(rst != None, rst match { case None => Some("ALREADYEXISTS") case _ => None} )
  }

  @RequestMapping(value=Array("server/{fqdn:.+}"), method = Array(RequestMethod.DELETE))
  @ResponseBody
  def deleteServer(@PathVariable fqdn:String):Result = {
    Result(deleteServer(getUser().id, fqdn))
  }

  @RequestMapping(value=Array("domain"), method = Array(RequestMethod.GET))
  @ResponseBody
  def domain():Seq[Domain] = {
    getDomains(getUser().id)
  }

}

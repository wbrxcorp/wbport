package com.wbport.user

import com.walbrix.spring.mvc.{CRUDWithAuthentication, LoginRequestHandler}
import com.walbrix.spring.{HttpContextSupport, VelocitySupport, EmailSupport}
import com.wbport._
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation._
import scalikejdbc._

case class Auth(email:String,password:String)
case class ChangePassword(password:Option[String],newPassword:String)
case class Quit(password:Option[String])

/**
 * Created by shimarin on 14/11/12.
 */
@Controller
@Transactional
@RequestMapping(Array(""))
class RequestHandler extends DAO with Authentication with EmailSupport with VelocitySupport with HttpContextSupport with LoginRequestHandler[User,Int] {
  implicit def boolean2result(success:Boolean):Result[Nothing] = Result(success)

  @RequestMapping(value=Array("info"), method = Array(RequestMethod.GET))
  @ResponseBody
  def info():Map[String,Any] = {
    getUserOpt().map { user =>
      Map("email"->user.email, "passwordPresent"->user.passwordPresent)
    }.getOrElse(Map("email"->None))
  }

  @RequestMapping(value=Array("signup"), method = Array(RequestMethod.POST),consumes=Array("application/json"))
  @ResponseBody
  def signup(@RequestBody json:Map[String,AnyRef]):Result[String] = {
    val email = json("email").asInstanceOf[String]
    getUserHasPassword(email) match {
      case Some(user) => Result.fail("ALREADYEXISTS")
      case None =>
        val authToken = com.walbrix.generateAuthToken(email)
        val rst = apply(sql"merge into users(email,auth_token,auth_token_expires_at) key(email) values(${email},${authToken},DATEADD('DAY', 1, now()))".update())
        if (rst < 1) return Result.fail
        val sender = createMailSender()
        val message = sender.createJisMailMessage
        message.setFrom(getSystemEmailFrom())
        message.setTo(email)
        val variables = Map("context_url"->getContextURL(),"auth_token"->authToken)
        val subject = evaluate(getSystemConfig("confirm_email_subject").getOrElse("wbport.com registration confirmation"), variables)
        message.setSubject(subject)
        val text = mergeTemplateIntoString("confirm-email.vm", variables)
        message.setText(text)
        try {
          sender.send(message)
          createMailLog(email, Some(subject), true)
          Result.success
        }
        catch {
          case e:Exception =>
            createMailLog(email, Some(subject), false, Some(e.getMessage))
            Result.fail("MAILSENDFAIL")
        }
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
  def doConfirm(@PathVariable authToken:String):Result[Nothing] = {
    val rst = getUserHasNoPasswordByAuthToken(authToken).map { user =>
      loginWithFreshAuthToken(user.id)
    }.getOrElse(false)
    rst
  }

  @RequestMapping(value=Array("server"), method = Array(RequestMethod.GET))
  @ResponseBody
  def server():Seq[Server] = getServers(getUser().id)

  @RequestMapping(value=Array("server"), method = Array(RequestMethod.POST),consumes=Array("application/json"))
  @ResponseBody
  def server(@RequestBody json:Map[String,AnyRef]):Result[String] = {
    val rst = createServer(getUser().id, json("fqdn").asInstanceOf[String])
    Result(rst != None, rst match { case None => Some("ALREADYEXISTS") case _ => None} )
  }

  @RequestMapping(value=Array("server/{fqdn:.+}"), method = Array(RequestMethod.DELETE))
  @ResponseBody
  def deleteServer(@PathVariable fqdn:String):Result[Nothing] = deleteServer(getUser().id, fqdn)

  @RequestMapping(value=Array("domain"), method = Array(RequestMethod.GET))
  @ResponseBody
  def domain():Seq[String] = getDomains(getUser().id)

  @RequestMapping(value=Array("password"), method = Array(RequestMethod.POST))
  @ResponseBody
  def changePassword(@RequestBody json:ChangePassword):Result[String] = {
    val user = getUser()
    if (user.passwordPresent) {
      if (!json.password.map(checkPassword(user.email, _) != None).getOrElse(false))
        return Result.fail("INVALIDPASSWORD")
    }
    update(sql"update users set password=${com.walbrix.encryptPassword(json.newPassword)} where id=${user.id}") match {
      case rst if rst > 0 =>
        loginWithFreshAuthToken(user.id)
        Result.success
      case _ => Result.fail("UPDATEFAIL")
    }
  }

  @RequestMapping(value=Array("quit"), method = Array(RequestMethod.POST))
  @ResponseBody
  def quit(@RequestBody json:Quit):Result[String] = {
    val user = getUser()
    if (user.passwordPresent) {
      if (!json.password.map(checkPassword(user.email, _) != None).getOrElse(false))
        return Result.fail("INVALIDPASSWORD")
    }
    update(sql"delete from servers where user_id=${user.id}")
    update(sql"delete from domains where user_id=${user.id}")
    update(sql"delete from users where id=${user.id}")  match {
      case rst if rst > 0 =>
        logout()
        Result.success
      case _ => Result.fail("DELETEFAIL")
    }
  }
}

@Controller
@Transactional
@RequestMapping(Array("serverCRUD"))
class CRUDRequestHandler extends CRUDWithAuthentication[Server, Int, User, Int] with Authentication {
  override def create(entity: Server, user: User): Option[Int] = {
    update(sql"insert into servers(fqdn,user_id) values(${entity.fqdn},${user.id}})") match {
      case x if x > 0 => int(sql"select last_insert_id")
      case _ => None
    }
  }

  override def update(id: Int, entity: Server, user: User): Boolean = {
    update(sql"update servers set fqdn=${entity.fqdn} where id=${id} and user_id=${user.id}") > 0
  }

  override def get(offset: Int, limit: Int, ordering: Option[String], user: User): (Int, Seq[Server]) = {
    val servers = list(sql"select * from servers where user_id=${user.id} order by id limit ${limit} offset ${offset}".map { row=>
      Server(id=row.int("id"),fqdn=row.string("fqdn"))
    })
    val count = int(sql"select count(*) from servers where user_id=${user.id}").get
    (count, servers)
  }

  override def get(id: Int, user: User): Option[Server] = {
    single(sql"select * from servers where user_id=${user.id} and id=${id}".map { row =>
      Server(id=row.int("id"),fqdn=row.string("fqdn"))
    })
  }

  override def delete(id: Int, user: User): Boolean =
    update(sql"delete from servers where user_id=${user.id} and id=${id}") > 0

  override def toIdType(id: String):Int = id.toInt
}

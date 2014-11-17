package com.walbrix.spring.mvc

import org.springframework.web.bind.annotation._

/**
 * Created by shimarin on 14/11/17.
 */
case class Page[T](count:Int,offset:Int,limit:Int,items:Seq[T])

abstract trait CRUD[T,TID] extends HttpErrorStatus {
  def defaultLimit() = 20
  def toIdType(id:String):TID

  @RequestMapping(value=Array(""), method=Array(RequestMethod.POST), consumes=Array("application/json"))
  @ResponseBody
  def _create(@RequestBody entity:T):Result[TID] =
    create(entity).map(Success(_)).getOrElse(Fail())

  def create(entity:T):Option[TID]

  @RequestMapping(value=Array(""), method=Array(RequestMethod.GET))
  @ResponseBody
  def _get(@RequestParam(value="offset",defaultValue="0") offset:Int,
           @RequestParam(value="limit",required=false) _limit:java.lang.Integer,
            @RequestParam(value="ordering",required=false) ordering:String):Page[T] = {
    val limit = if (_limit == null) defaultLimit() else _limit.toInt
    val rst = get(offset, limit, Option(ordering))
    Page(rst._1, offset, limit, rst._2)
  }

  def get(offset:Int, limit:Int, ordering:Option[String] = None):(Int, Seq[T])

  @RequestMapping(value=Array("{id}"), method=Array(RequestMethod.GET))
  @ResponseBody
  def _get(@PathVariable(value="id") id:String):T = {
    get(toIdType(id)).getOrElse(raiseNotFound)
  }

  def get(id:TID):Option[T]

  @RequestMapping(value=Array("{id}"), method=Array(RequestMethod.POST), consumes=Array("application/json"))
  @ResponseBody
  def _update(@PathVariable("id") id:String, @RequestBody entity:T):Result[String] = {
    (try {
      update(toIdType(id),entity)
    }
    catch  {
      case e:Exception => return Fail(e.getMessage)
    }) match {
      case true => Success()
      case false => raiseNotFound
    }
  }

  def update(id:TID, entity:T):Boolean

  @RequestMapping(value=Array("{id}"), method=Array(RequestMethod.DELETE))
  @ResponseBody
  def _delete(@PathVariable("id") id:String):Result[String] = {
    (try {
      delete(toIdType(id))
    }
    catch  { case e:Exception => return Fail(e.getMessage) })
    match {
      case true => Success()
      case false => raiseNotFound
    }
  }

  def delete(id:TID):Boolean
}

trait CRUDWithAuthentication[T,TID,U,UID] extends CRUD[T,TID] with Authentication[U,UID] {
  override def create(entity: T): Option[TID] = create(entity, getUser())
  def create(entity:T, user:U):Option[TID]

  override def update(id: TID, entity: T): Boolean = update(id, entity, getUser())
  def update(id:TID, entity:T, user:U):Boolean

  override def get(offset: Int, limit: Int, ordering: Option[String]): (Int, Seq[T]) = get(offset, limit, ordering, getUser())
  def get(offset: Int, limit: Int, ordering: Option[String], user:U):(Int, Seq[T])

  override def get(id: TID): Option[T] = get(id, getUser())
  def get(id:TID, user:U):Option[T]

  override def delete(id: TID): Boolean = delete(id, getUser())
  def delete(id:TID, user:U):Boolean
}

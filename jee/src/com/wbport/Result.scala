package com.wbport

/**
 * Created by shimarin on 14/11/16.
 */
case class Result[T](success:Boolean, info:Option[T] = None)

case object Result {
  def success[T] = Result[T](true, None)
  def success[T](info:T) = Result(true, Some(info))
  def fail[T] = Result[T](false, None)
  def fail[T](info:T) = Result(false, Some(info))
}
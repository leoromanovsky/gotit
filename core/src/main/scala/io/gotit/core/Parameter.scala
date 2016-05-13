package io.gotit.core

import scala.pickling.Defaults._, scala.pickling.json._

case class ParameterDb(
  id: Option[Int],
  value: String,
  clazz: String
) {
  def unpickle: Parameter = {
    val clz = Class.forName("Int")
    value.unpickle[clz]
  }
}

class Parameter
class IntParameter(var value: Int) extends Parameter
class FloatParameter(var value: Float) extends Parameter
class StringParameter(var value: String) extends Parameter

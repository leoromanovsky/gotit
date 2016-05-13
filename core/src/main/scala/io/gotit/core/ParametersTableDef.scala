package io.gotit.core

import slick.driver.MySQLDriver.api._

class ParametersTableDef(tag: Tag) extends Table[ParameterDb](tag, "parameters") {
  def id = column[Int]("id", O.PrimaryKey)
  def value = column[String]("value")
  def clazz = column[String]("value")
  def * = (id.?, value, clazz) <> (ParameterDb.tupled, ParameterDb.unapply)
}

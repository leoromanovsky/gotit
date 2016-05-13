package io.gotit.core

import scala.concurrent.{Await, Future}
import scala.collection.mutable.Map
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class LeoException(message: String) extends Exception
case class ParameterCacheEntry(param: Parameter, var timestamp: Long)
class ParameterCache(maxEntries: Int = 100) {
  var cache: Map[ParameterName, ParameterCacheEntry] = Map()

  // TODO: make threadsafe.
  def fetch(name: ParameterName): Future[Option[Parameter]] = {
    println("ParameterCache.fetch", name)

    cache.get(name) match {
      case Some(param) =>
        println("cache hit", name)

        param.timestamp = currentTimeMillis
        cache.update(name, param)
        Future.successful(Some(param.param))
      case None =>
        println("cache miss", name)
        /*
        The cached entry does not exist, load from DB.
        Save it to the cache and return the result.
         */

        DatabaseLoader.load(name).flatMap {
          case Some(dbValue) =>
            println(s"found ${name} in db")

            // The value exists in the database.
            val cacheEntry =
              ParameterCacheEntry(dbValue.param, currentTimeMillis)

            evictOldestEntry
            cache.put(name, cacheEntry)

            Future.successful(Some(dbValue.param))
          case None =>
            println(s"not found ${name} in db")
            Future.failed(new LeoException("Nothing in the database!"))
        }
    }
  }

  private def evictOldestEntry = {
    if (cache.size > maxEntries) {
      println("cache is full")
      val oldest = cache.minBy(_._2.timestamp)
      println("evicting", oldest)
      cache.remove(oldest._1)
    }
  }

  private def currentTimeMillis: Long = {
    System.currentTimeMillis()
  }
}

object DatabaseLoader {
  def load(name: ParameterName): Future[Option[ParameterDb]] = {
    println("DatabaseLoader.load", name)

    Future.successful(
        Some(ParameterDb(Some(1), Parameter(name, "foo", "Long"))))
  }
}

case class ParameterName(value: String) {
  override def toString: String = { value }
}
case class ParameterDb(
    id: Option[Int],
    param: Parameter
)

case class Parameter(name: ParameterName,
                     value: String,
                     clazz: String)
//class IntParameter(var value: Int) extends Parameter
//class FloatParameter(var value: Float) extends Parameter
//class StringParameter(var value: String) extends Parameter

object ParametersApp {
  def main(args: Array[String]) {
    val cache = new ParameterCache(4)

    // Test cache hit
    println(
        "ParametersApp.max_retries",
        Await.result(cache.fetch(ParameterName("max_retries")), Duration.Inf))
    println(
        "ParametersApp.max_retries",
        Await.result(cache.fetch(ParameterName("max_retries")), Duration.Inf))

    // Fill up the cache
    Await.result(cache.fetch(ParameterName("max_retries1")), Duration.Inf)
    Await.result(cache.fetch(ParameterName("max_retries2")), Duration.Inf)
    Await.result(cache.fetch(ParameterName("max_retries3")), Duration.Inf)
    Await.result(cache.fetch(ParameterName("max_retries4")), Duration.Inf)
    Await.result(cache.fetch(ParameterName("max_retries5")), Duration.Inf)
    /*
    (DatabaseLoader.load,max_retries5)
    found max_retries5 in db
    cache is full
    (evicting,(max_retries,ParameterCacheEntry(Parameter(max_retries,foo,Long),1463135075494)))
   */
  }
}

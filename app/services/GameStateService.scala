package services

import java.util.UUID
import javax.inject.*
import scala.collection.mutable

@Singleton
class GameStateService {
  private val instances: mutable.Map[String, GameStateInstance] = mutable.Map()

  private def newKey: String = {
    UUID.randomUUID().toString
  }

  private def newInstance: (String, GameStateInstance) = {
    val instance = GameStateInstance()
    val key = newKey
    instances(key) = instance

    (key, instance)
  }

  def getInstance(keyOption: Option[String]): (String, GameStateInstance) = {
    keyOption match {
      case Some(key) =>
        if instances.contains(key) then (key, instances(key))
        else newInstance
      case None => newInstance
    }
  }
}

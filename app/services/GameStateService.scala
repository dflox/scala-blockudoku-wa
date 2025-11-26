package services

import java.util.UUID
import javax.inject.*
import scala.collection.mutable

@Singleton
class GameStateService {
  private val instances: mutable.Map[String, GameStateInstance] = mutable.Map()

  private def newKey: String = {
    //UUID.randomUUID().toString
    "0"
  }

  private def newInstance: (String, GameStateInstance) = {
    val instance = GameStateInstance()
    val key = newKey
    instances(key) = instance

    (key, instance)
  }
  
  def setInstance(key: Option[String], instance: GameStateInstance): String = {
    val actualKey = key.getOrElse(newKey)
    instances(actualKey) = instance
    actualKey
  }

  def getInstance(keyOption: Option[String]): (String, GameStateInstance) = {
    val key = "0"

    if instances.contains(key) then (key, instances(key))
    else newInstance
  }
}

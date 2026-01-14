package services

import java.util.UUID
import javax.inject.*
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.FiniteDuration

@Singleton
class GameStateService {

  private val instances: TrieMap[String, GameStateInstance] = TrieMap.empty

  private def newKey: String = {
    UUID.randomUUID().toString
  }

  private def newInstance(key: String): (String, GameStateInstance) = {
    val instance = GameStateInstance(key)
    instances(key) = instance

    (key, instance)
  }
  
  def setInstance(key: Option[String], instance: GameStateInstance): String = {
    val actualKey = key.getOrElse(newKey)
    instances(actualKey) = instance
    actualKey
  }

  def getInstance(keyOption: Option[String]): (String, GameStateInstance) = {
    keyOption match {
      case Some(key) =>
        if instances.contains(key) then (key, instances(key))
        else newInstance(key)
      case None => newInstance(newKey)
    }
  }
  
  def removeInstance(key: String): Unit = {
    instances.remove(key)
  }
  
  def cleanUpOldInstances(maxAge: FiniteDuration): Unit = {
    val now = System.currentTimeMillis()
    val keysToRemove = instances.collect {
      case (key, instance) if (now - instance.getLastTimeUsed) > maxAge.toMillis => key
    }
    keysToRemove.foreach(instances.remove)
  }
}

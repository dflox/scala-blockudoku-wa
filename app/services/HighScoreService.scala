package services

import javax.inject.*
import scala.collection.concurrent.TrieMap

@Singleton
class HighScoreService {
  private val highScores = TrieMap[String, Int]()

  def getHighScore(profileId: String): Option[Int] = {
    highScores.get(profileId)
  }

  def setHighScore(profileId: String, score: Int): Unit = {
    val currentHighScore = getHighScore(profileId)
    currentHighScore match {
      case Some(existingScore) if score > existingScore =>
        highScores.update(profileId, score)
      case None =>
        highScores.put(profileId, score)
      case _ => // Do nothing if the new score is not higher
    }
  }
}

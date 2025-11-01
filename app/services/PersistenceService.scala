package services

import java.io.File
import javax.inject.Singleton

@Singleton
class PersistenceService {
  private val exportPath: String = "public/games/"

  def saveGameState(state: GameStateInstance, gameKey: String): File = {
    val serializedGameState = state.toJson
    val file = new File(s"$exportPath$gameKey.blockudoku")
    val parent = file.getParentFile
    if (parent != null && !parent.exists()) parent.mkdirs()
    val pw = new java.io.PrintWriter(file)
    try {
      pw.write(serializedGameState)
    } finally {
      pw.close()
    }
    file
  }

  def loadGameState(gameFile: File): Option[GameStateInstance] = {
    if (gameFile.exists()) {
      val source = scala.io.Source.fromFile(gameFile)
      val jsonString = try source.mkString finally source.close()
      Some(GameStateInstance.fromJson(jsonString))
    } else {
      None
    }
  }
}

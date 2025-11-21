package services

import play.api.libs.ws.*
import model.GameData
import play.api.Configuration
import play.api.libs.json.Json

import javax.inject.*

@Singleton
class SocketService @Inject()(ws: WSClient, config: Configuration) {
  private val url = config.get[String]("socketService.url")

  def gameStateUpdate(sessionId: String, gameData: GameData): Unit =
    ws.url(url + "/game")
      .post(Json.toJson(Map(
        "sessionId" -> Json.toJson(sessionId),
        "state" -> Json.toJson(gameData)
      )))
}

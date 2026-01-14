package controllers

import model.PlacementHistory
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.profile.{CommonProfile, ProfileManager, UserProfile}
import org.pac4j.play.context.PlayFrameworkParameters
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.*
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import services.*
import util.*

import javax.inject.*
import scala.concurrent.Future
import scala.jdk.OptionConverters.*

@Singleton
class GameController @Inject()(val controllerComponents: SecurityComponents,
                               val gameStateService: GameStateService,
                               val socketService: SocketService,
                               val highScoreService: HighScoreService,
                               val sessionStore: SessionStore) extends Security[CommonProfile] {

  def placeElement: Action[JsValue] = Action.async(parse.json) { implicit request => {
    val webContext = controllerComponents.config.getWebContextFactory
      .newContext(PlayFrameworkParameters(request.asJava))

    val profileManager = new ProfileManager(webContext, sessionStore)

    val profile = profileManager.getProfile.toScala

    request.body.validate[List[PlacementHistory]].fold(
      error => {
        Future.successful(BadRequest("Invalid data. " + error.toString))
      },
      placementHistories => {
        val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
        Future.successful(handlePlaceElement(placementHistories, gameKey, gameState,
          socketService, profile))
      }
    )
  }
  }

  private def handlePlaceElement(placementHistories: List[PlacementHistory],
                                 gameKey: String,
                                 gameState: GameStateInstance,
                                 socketService: SocketService,
                                 profile: Option[UserProfile]
                                )
  : Result = {
    gameState.updatePlacementHistory(placementHistories)
    val gameData = GameDataBuilder.build(gameKey, gameState)

    socketService.gameStateUpdate(gameKey, gameData)

    if (profile.isDefined) {
      val userId = profile.get.getId
      highScoreService.setHighScore(userId, gameState.getScore)
    }
    
    Ok(Json.toJson(gameData))
      .withGameStateKeyCookie(gameKey)
  }
}

package controllers

import model.PlacementHistory
import play.api.*
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import services.*
import util.*

import javax.inject.*
import scala.concurrent.Future

@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents,
                               val gameStateService: GameStateService, val 
                               socketService: SocketService,
                               homeController: HomeController) extends BaseController {

  def placeElement: Action[JsValue] = Action.async(parse.json) { implicit request => {
    request.body.validate[List[PlacementHistory]].fold(
      error => {
        Future.successful(BadRequest("Invalid data. " + error.toString))
      },
      placementHistories => {
        val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
        Future.successful(handlePlaceElement(placementHistories, gameKey, gameState, socketService))
      }
    )
  }
  }

  private def handlePlaceElement(placementHistories: List[PlacementHistory],
                                 gameKey: String,
                                 gameState: GameStateInstance,
                                 socketService: SocketService
                                )
  : Result = {
    gameState.updatePlacementHistory(placementHistories)
    val gameData = GameDataBuilder.build(gameKey, gameState)

    socketService.gameStateUpdate(gameKey, gameData)
    
    Ok(Json.toJson(gameData))
      .withGameStateKeyCookie(gameKey) 
  }
}

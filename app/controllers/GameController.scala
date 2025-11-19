package controllers

import model.ElementPlacement
import play.api.*
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import services.*
import util.*
import model.UniversalGridPreview.* 
import model.ElementPlacement.*
import scala.concurrent.Future

import javax.inject.*

@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents,
                               val gameStateService: GameStateService) extends BaseController {

  def placeElement: Action[JsValue] = Action.async(parse.json) { implicit request => {
    request.body.validate[ElementPlacement].fold(
      error => {
        Future.successful(BadRequest("Invalid data. " + error.toString))
      },
      elementPlacement => {
        val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
        Future.successful(handlePlaceElement(elementPlacement, gameKey, gameState))
      }
    )
  }
  }

  private def handlePlaceElement(elementPlacement: ElementPlacement,
                                 gameKey: String, 
                                 gameState: GameStateInstance)
  : Result = {
    gameState.placeElement(elementPlacement.positionIndex, elementPlacement.elementIndex)
    Ok(Json.toJson(gameState.getUniversalGridPreviewGenerator.getUniversalGridPreview)).withGameStateKeyCookie(gameKey)
  }
}

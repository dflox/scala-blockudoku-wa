package controllers

import model.GameData
import play.api.*
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import services.{GameStateInstance, GameStateService}
import util.*

import javax.inject.*
import scala.util.{Failure, Success}

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val gameStateService: GameStateService) extends BaseController {

  def getGame: Action[AnyContent] = Action { implicit request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    
    val gameData = GameDataBuilder.build(gameKey, gameState)
    Ok(Json.toJson(gameData))
      .withGameStateKeyCookie(gameKey)
  }
  }

  def newGame(): Action[AnyContent] = Action { implicit
                                               request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameStateService.setInstance(Some(gameKey), GameStateInstance(gameKey))
    Redirect(routes.HomeController.getGame)
      .withGameStateKeyCookie(gameKey)
  }
  }

  def nextColor(): Action[AnyContent] = Action { implicit
                                                 request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameState.nextColorScheme()
    Ok("")
      .withGameStateKeyCookie(gameKey)
  }
  }

  def prevColor(): Action[AnyContent] = Action { implicit
                                                 request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameState.prevColorScheme()
    Ok("")
      .withGameStateKeyCookie(gameKey)
  }
  }

  def setColor(ind: Int): Action[AnyContent] = Action { implicit
                                                   request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameState.setColorScheme(ind) match {
      case Success(_) =>
        Ok("")
          .withGameStateKeyCookie(gameKey)
      case Failure(_) =>
        BadRequest("Invalid color index")
    }
  }
  }

  def setNumElements(num: Int): Action[AnyContent] = Action { implicit
                                                   request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameState.setNumElements(num) match {
      case Success(_) =>
        Ok("")
          .withGameStateKeyCookie(gameKey)
      case Failure(_) =>
        BadRequest("Invalid number of elements")
    }
  }
  }
}
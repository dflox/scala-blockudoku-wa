package controllers

import jakarta.inject.Singleton
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.api.*
import services.GameStateService
import util.*

import javax.inject.Inject

@Singleton
class SessionController @Inject()(val controllerComponents: ControllerComponents, val gameStateService: GameStateService) extends
                                                                                   BaseController {
  def joinSession(sessionId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.HomeController.getGame)
      .withGameStateKeyCookie(sessionId)
  }

  def newSession: Action[AnyContent] = Action { implicit request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameStateService.removeInstance(gameKey)
    Redirect(routes.HomeController.getGame)
      .withGameStateKeyCookie(gameKey)
  }
  }
}

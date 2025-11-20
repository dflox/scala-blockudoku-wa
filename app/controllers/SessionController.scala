package controllers

import jakarta.inject.Singleton
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.api.*
import util.*

import javax.inject.Inject

@Singleton
class SessionController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def joinSession(sessionId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.HomeController.getGame)
      .withGameStateKeyCookie(sessionId)
  }
}

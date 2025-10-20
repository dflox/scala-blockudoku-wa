package controllers

import blockudoku.views.console.composed.Direction.*
import play.api.*
import play.api.mvc.*
import services.GameStateService
import util.ColorUtilities

import javax.inject.*

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val colorUtilities: ColorUtilities, val gameStateService: GameStateService) extends BaseController {
  
  private def content: String = {
    val currentConsoleContent = gameStateService.content
    colorUtilities.convertConsoleToHTML(currentConsoleContent)
  }

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(content))
  }

  def direction(dir: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    gameStateService.navigate(dir match {
      case "up" => Up
      case "down" => Down
      case "left" => Left
      case "right" => Right
    })
    Redirect(routes.HomeController.index())
  }

  def select(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    gameStateService.select()
    Redirect(routes.HomeController.index())
  }
}

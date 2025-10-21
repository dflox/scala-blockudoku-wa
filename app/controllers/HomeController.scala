package controllers

import blockudoku.views.console.composed.Direction.*
import play.api.*
import play.api.mvc.*
import util.{ColorUtilities, HtmlUtilities}
import services.GameStateService

import javax.inject.*

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val colorUtilities: ColorUtilities,
                               val htmlUtilities: HtmlUtilities,
                               val gameStateService: GameStateService) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val grid = gameStateService.getGrid
    Ok(views.html.index(grid, htmlUtilities))
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

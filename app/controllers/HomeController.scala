package controllers

import blockudoku.models.Tile
import blockudoku.views.console.composed.Direction.*
import play.api.*
import play.api.mvc.*
import services.GameStateService
import util.{ColorUtilities, HtmlUtilities}

import javax.inject.*

val COOKIE_KEY = "state-key"
extension (result: Result)
  def withGameStateKeyCookie(key: String): Result =
    result.withCookies(Cookie("username", key))

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val colorUtilities: ColorUtilities,
                               val htmlUtilities: HtmlUtilities,
                               val gameStateService: GameStateService) extends BaseController {
  private def getStateKeyCookie(implicit request: Request[AnyContent]): Option[String] = request.cookies
    .get(COOKIE_KEY) match {
    case Some(cookie) => Some(cookie.value)
    case None => None
  }

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)

    val grid = gameState.getGrid
    val elements = gameState.getElements
    Ok(views.html.index(grid, elements, htmlUtilities))
      .withGameStateKeyCookie(key)
  }

  def direction(dir: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)

    gameState.navigate(dir match {
      case "up" => Up
      case "down" => Down
      case "left" => Left
      case "right" => Right
    })
    Redirect(routes.HomeController.index())
      .withGameStateKeyCookie(key)
  }

  def selectElement(ind: Int): Action[AnyContent] = Action { implicit
                                                             request: Request[AnyContent] =>
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)

    gameState.selectElement(ind)
    Redirect(routes.HomeController.index())
      .withGameStateKeyCookie(key)
  }

  def placeElement(tileIndex: Int): Action[AnyContent] = Action { implicit
                                                                  request: Request[AnyContent] =>
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)
    
    gameState.placeElement(tileIndex)
    Redirect(routes.HomeController.index())
      .withGameStateKeyCookie(key)
  }
  
  def getPreview(tileIndex: Int): Action[AnyContent] = Action { implicit
                                                 request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)
    
    val previewGrid = gameState.getPreviewGrid(tileIndex)

    val tilesToUpdate: Vector[Tile]

    Ok(tilesToUpdate, htmlUtilities)
  }
}

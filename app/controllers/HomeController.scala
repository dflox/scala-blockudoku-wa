package controllers

import play.api.*
import play.api.mvc.*
import services.GameStateService
import util.HtmlUtilities

import javax.inject.*

val COOKIE_KEY = "state-key"
extension (result: Result)
  def withGameStateKeyCookie(key: String): Result =
    result.withCookies(Cookie(COOKIE_KEY, key))

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val htmlUtilities: HtmlUtilities,
                               val gameStateService: GameStateService) extends BaseController {
  private def getStateKeyCookie(implicit request: Request[AnyContent]): Option[String] = request
    .cookies
    .get(COOKIE_KEY) match {
    case Some(cookie) => Some(cookie.value)
    case None => None
  }

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)

    val universalGridPreview = gameState.getUniversalGridPreview
    val elements = gameState.getElements
    val selectedElement = gameState.getSelectedElement
    Ok(views.html.index(universalGridPreview, elements, htmlUtilities,
      gameState.getColorIndex))
      .withGameStateKeyCookie(key)
  }
  }

  def newGame(): Action[AnyContent] = Action { implicit
                                               request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(None)
    Redirect(routes.HomeController.index())
      .withGameStateKeyCookie(key)
  }
  }

  def selectElement(ind: Int): Action[AnyContent] = Action { implicit
                                                             request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)

    gameState.selectElement(ind)
    Ok(views.html.carousel(
      gameState.getUniversalGridPreview,
      gameState.getElements,
      htmlUtilities,
      gameState.getColorIndex))
      .withGameStateKeyCookie(key)  }
  }

  def placeElement(tileIndex: Int): Action[AnyContent] = Action { implicit
                                                                  request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)

    gameState.placeElement(tileIndex)

    if(gameState.getSelectedElement.isEmpty)
      Ok(views.html.carousel(
        gameState.getUniversalGridPreview,
        gameState.getElements,
        htmlUtilities,
        gameState.getColorIndex))
        .withGameStateKeyCookie(key)
    else
      Ok("")
        .withGameStateKeyCookie(key)
  }
  }

  def nextColor(): Action[AnyContent] = Action { implicit
                                                 request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameState.nextColorScheme()
    Ok("")
      .withGameStateKeyCookie(key)
  }
  }

  def prevColor(): Action[AnyContent] = Action { implicit
                                                 request: Request[AnyContent] => {
    val (key, gameState) = gameStateService.getInstance(getStateKeyCookie)
    gameState.prevColorScheme()
    Ok("")
      .withGameStateKeyCookie(key)
  }
  }
}
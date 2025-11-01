package controllers

import play.api.*
import play.api.mvc.*
import services.GameStateService
import util.*

import javax.inject.*

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val htmlUtilities: HtmlUtilities,
                               val gameStateService: GameStateService) extends BaseController {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    
    val universalGridPreview = gameState.getUniversalGridPreview
    val elements = gameState.getElements
    val selectedElement = gameState.getSelectedElement
    Ok(views.html.index(universalGridPreview, elements, htmlUtilities,
      gameState.getColorIndex))
      .withGameStateKeyCookie(gameKey)
  }
  }

  def newGame(): Action[AnyContent] = Action { implicit
                                               request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(None)
    Redirect(routes.HomeController.index())
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
}
package controllers

import play.api.*
import play.api.mvc.*
import services.GameStateService
import util.*

import javax.inject.*

@Singleton
class GameController @Inject()(val controllerComponents: ControllerComponents,
                               val htmlUtilities: HtmlUtilities,
                               val gameStateService: GameStateService) extends BaseController {

  def selectElement(ind: Int): Action[AnyContent] = Action { implicit
                                                             request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)

    gameState.selectElement(ind)
    Ok(views.html.carousel(
      gameState.getUniversalGridPreview,
      gameState.getElements,
      htmlUtilities,
      gameState.getColorIndex))
      .withGameStateKeyCookie(gameKey)
  }
  }

  def placeElement(tileIndex: Int): Action[AnyContent] = Action { implicit
                                                                  request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)

    gameState.placeElement(tileIndex)

    if (gameState.getSelectedElement.isEmpty)
      Ok(views.html.carousel(
        gameState.getUniversalGridPreview,
        gameState.getElements,
        htmlUtilities,
        gameState.getColorIndex))
        .withGameStateKeyCookie(gameKey)
    else
      Ok("")
        .withGameStateKeyCookie(gameKey)
  }
  }

}

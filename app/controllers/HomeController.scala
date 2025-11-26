package controllers

import model.GameData
import org.apache.pekko.actor.{Actor, ActorRef, ActorSystem, Props}
import play.api.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.*
import services.GameStateService
import util.*

import javax.inject.*
import scala.util.{Failure, Success}

@Singleton
class HomeController @Inject()(implicit actorSystem: ActorSystem, val controllerComponents: ControllerComponents,
                               val htmlUtilities: HtmlUtilities,
                               val gameStateService: GameStateService) extends BaseController {

  def getGame: Action[AnyContent] = Action { implicit request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    
    val grid = gameState.getGrid
    val universalGridPreview = gameState.getUniversalGridPreviewGenerator.getUniversalGridPreview
    val elements = gameState.getElements
    val gameData = GameData(elements, universalGridPreview, grid, gameState.getScore,
      gameState.getColorIndex, gameKey)
    Ok(Json.toJson(gameData))
      .withGameStateKeyCookie(gameKey)
  }
  }

  def newGame(): Action[AnyContent] = Action { implicit
                                               request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(None)
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

  def socket: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connect received")
      WebSocketActorFactory.create(out)
    }
  }

  private object WebSocketActorFactory {
    def create(out: ActorRef): Props = {
      Props(new WebsocketActor(out))
    }
  }

  private class WebsocketActor(out: ActorRef) extends Actor {
    private val (gameKey, gameState) = gameStateService.getInstance(None)

    gameState.onPlace.addListener( () => sendState() )

    private def sendState(): Unit = {
      val grid = gameState.getGrid
      val universalGridPreview = gameState.getUniversalGridPreviewGenerator.getUniversalGridPreview
      val elements = gameState.getElements
      val gameData = GameData(elements, universalGridPreview, grid, gameState.getScore,
        gameState.getColorIndex, gameKey)

      out ! Json.toJson(gameData).toString
    }

    override def receive: Receive = {
      case msg: String =>
        val grid = gameState.getGrid
        val universalGridPreview = gameState.getUniversalGridPreviewGenerator.getUniversalGridPreview
        val elements = gameState.getElements
        val gameData = GameData(elements, universalGridPreview, grid, gameState.getScore,
          gameState.getColorIndex, gameKey)

        out ! Json.toJson(gameData).toString
    }
  }
}
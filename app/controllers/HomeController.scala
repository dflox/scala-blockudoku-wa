package controllers

import model.GameData
import play.api.*
import play.api.libs.json.Json
import play.api.mvc.*
import services.{GameStateInstance, GameStateService, HighScoreService}
import util.*
import org.pac4j.play.scala.{SecureAction, Security, SecurityComponents}
import org.pac4j.core.profile.CommonProfile

import javax.inject.*
import scala.util.{Failure, Success}

@Singleton
class HomeController @Inject()(val controllerComponents: SecurityComponents,
                               val gameStateService: GameStateService,
                               val highScoreService: HighScoreService) extends
                                                                        Security[CommonProfile] {

  def getHighScore: Action[AnyContent] = Secure("CookieClient") { implicit request =>
    profiles.headOption match {
      case Some(user) =>
        val highScore = highScoreService.getHighScore(user.getId)
        highScore match {
          case Some(score) =>
            Ok(s"$score")
          case None =>
            val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
            highScoreService.setHighScore(user.getId, gameState.getScore)
            Ok(s"${highScoreService.getHighScore(user.getId).get}")
              .withGameStateKeyCookie(gameKey)
        }
      case None =>
        Unauthorized("No profile found")
    }
  }

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
}
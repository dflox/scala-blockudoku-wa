package controllers

import controllers.Execution.trampoline
import play.api.Environment
import play.api.libs.Files
import play.api.mvc.*
import services.{GameStateService, PersistenceService}
import util.*

import java.io.File
import java.nio.file.Paths
import javax.inject.*

@Singleton
class FileController @Inject()(val controllerComponents: ControllerComponents,
                               private val htmlUtilities: HtmlUtilities,
                               private val gameStateService: GameStateService,
                               private val persistenceService: PersistenceService) extends BaseController {

  def uploadGame: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData)
    { request =>
      request.body.file("gameFile").map { upload =>
        val gameFile = upload.ref.path.toFile

        val gameState = persistenceService.loadGameState(gameFile)

        gameState match {
          case Some(state) =>
            val gameKey = gameStateService.setInstance(getStateKeyCookie(request), state)
            Redirect(routes.HomeController.index())
              .flashing("success" -> "true")
              .withGameStateKeyCookie(gameKey)
          case None =>
            BadRequest("Invalid game file")
        }
      }.getOrElse {
        BadRequest("Missing file")
      }
    }

  def downloadGame: Action[AnyContent] = Action { implicit
                                                  request: Request[AnyContent] => {
    val (gameKey, gameState) = gameStateService.getInstance(getStateKeyCookie)
    val gameFile = persistenceService.saveGameState(gameState, gameKey)

    Ok.sendFile(content = gameFile, inline = false, fileName = f => Some(f.getName), onClose = () => {
      gameFile.delete()
    })
      .withGameStateKeyCookie(gameKey)
  }
  }

}

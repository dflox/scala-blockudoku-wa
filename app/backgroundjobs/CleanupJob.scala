package backgroundjobs

import javax.inject.*
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}
import play.api.inject.ApplicationLifecycle
import org.apache.pekko.actor.ActorSystem
import services.GameStateService

@Singleton
class CleanupJob @Inject() (
                             actorSystem: ActorSystem,
                             lifecycle: ApplicationLifecycle,
                             val gameStateService: GameStateService
                           )(implicit ec: ExecutionContext) {

  private val maxAge = 1.minutes
  
  private val cancellable =
    actorSystem.scheduler.scheduleAtFixedRate(
      initialDelay = 1.minute,
      interval = 1.minutes
    ) { () =>
      runCleanup()
    }

  private def runCleanup(): Unit = {
    gameStateService.cleanUpOldInstances(maxAge)
  }

  lifecycle.addStopHook { () =>
    Future {
      cancellable.cancel()
    }
  }
}

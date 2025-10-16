package controllers

import blockudoku.commands.{CommandFactory, CommandInvoker}
import blockudoku.controllers.{ElementCollector, GridCollector}
import io.gitlab.freeeezee.yadis.ComponentContainer
import blockudoku.registerComponents
import blockudoku.services.{ApplicationThread, GridPreviewBuilder}
import blockudoku.views.console.composed.Direction.*
import blockudoku.views.console.composed.{ComposedConsoleFormatter, VerticalFrame}
import blockudoku.views.console.{ConsoleElementView, ConsoleGridView, ConsoleHeadlineView, ConsoleView}
import blockudoku.windows.{ConsoleWindow, FocusManager, Window}

import javax.inject.*
import play.api.*
import play.api.mvc.*
import util.ColorUtilities

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val colorUtilities: ColorUtilities) extends BaseController, Window {

  private val container = ComponentContainer().registerComponents().buildProvider()

  private val commandFactory = container.get[CommandFactory]
  private val commandInvoker = container.get[CommandInvoker]
  private val gridCollector = container.get[GridCollector]
  private val elementCollector = container.get[ElementCollector]
  private val focusManager = container.get[FocusManager]
  private val previewBuilder = container.get[GridPreviewBuilder]

  private val consoleViews = initializeViews()
  private var formatter = createFormatter(0, 0)

  private def initializeViews(): List[ConsoleView] = {
    var views: List[ConsoleView] = List()

    views = views :+ initializeHeadlineView()
    views = views :+ initializeGridView()
    views = views :+ initializeElementView()
    views
  }

  private def initializeHeadlineView(): ConsoleView = {
    val width = gridCollector.getGrid.xLength * 5 + 1
    ConsoleHeadlineView(width, focusManager, this)
  }

  private def initializeGridView(): ConsoleView = {
    ConsoleGridView(commandFactory, commandInvoker, gridCollector, elementCollector, focusManager, this, previewBuilder)
  }

  private def initializeElementView(): ConsoleView = {
    ConsoleElementView(commandFactory, commandInvoker, elementCollector, gridCollector, focusManager, this)
  }

  private def createFormatter(selectedX: Int, selectedY: Int): ComposedConsoleFormatter = {
    val verticalFrame = VerticalFrame(consoleViews.map(_.consoleElement))(0, isInteractable = true)
    ComposedConsoleFormatter.create(verticalFrame, selectedX, selectedY)
  }

  private def content: String = {
    formatter = createFormatter(formatter.selectedX, formatter.selectedY)
    colorUtilities.convertConsoleToHTML(formatter.content())
    //formatter.content()
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
    formatter = formatter.navigate(dir match {
      case "up" => Up
      case "down" => Down
      case "left" => Left
      case "right" => Right
    })
    Redirect(routes.HomeController.index())
  }

  def select(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    formatter.select()
    Redirect(routes.HomeController.index())
  }

  override def display(): Unit = {

  }

  override def setUpdated(): Unit = {

  }
}

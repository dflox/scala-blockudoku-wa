package services

import blockudoku.commands.{CommandFactory, CommandInvoker}
import blockudoku.controllers.{ElementCollector, GridCollector}
import blockudoku.models.Grid
import blockudoku.windows.{FocusManager, Window}
import io.gitlab.freeeezee.yadis.ComponentContainer
import blockudoku.registerComponents
import blockudoku.services.GridPreviewBuilder
import blockudoku.views.console.composed.{ComposedConsoleFormatter, Direction, VerticalFrame}
import blockudoku.views.console.{ConsoleElementView, ConsoleGridView, ConsoleHeadlineView, ConsoleView}

class GameStateInstance extends Window {
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
    ConsoleGridView(commandFactory, commandInvoker, gridCollector, elementCollector, focusManager,
      this, previewBuilder)
  }

  private def initializeElementView(): ConsoleView = {
    ConsoleElementView(commandFactory, commandInvoker, elementCollector, gridCollector,
      focusManager, this)
  }

  private def createFormatter(selectedX: Int, selectedY: Int): ComposedConsoleFormatter = {
    val verticalFrame = VerticalFrame(consoleViews.map(_.consoleElement))(0, isInteractable = true)
    ComposedConsoleFormatter.create(verticalFrame, selectedX, selectedY)
  }

  def content: String = {
    formatter = createFormatter(formatter.selectedX, formatter.selectedY)
    formatter.content()
  }

  def navigate(direction: Direction): Unit = {
    formatter = formatter.navigate(direction)
  }

  def select(): Unit = {
    formatter.select()
  }

  def getGrid: Grid = {
    gridCollector.getGrid
  }

  def getPreviewGrid(pos: Int): Grid = {
    previewBuilder.buildGrid(pos)
  }

  def getElements: List[blockudoku.models.Element] = {
    elementCollector.getElements
  }

  def selectElement(ind: Int): Unit = {
    val element = elementCollector.getElements(ind)
    val command = commandFactory.createSelectElementCommand(element)
    commandInvoker.execute(command)
  }

  def placeElement(tileIndex: Int): Unit = {
    val command = commandFactory.createSetElementCommand(elementCollector.getSelectedElement.get,
      tileIndex)
    commandInvoker.execute(command)
  }

  override def display(): Unit = {

  }

  override def setUpdated(): Unit = {

  }
}

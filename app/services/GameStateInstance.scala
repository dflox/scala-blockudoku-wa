package services

import blockudoku.commands.{CommandFactory, CommandInvoker}
import blockudoku.controllers.{ElementCollector, GridCollector}
import blockudoku.registerComponents
import blockudoku.services.GridPreviewBuilder
import blockudoku.views.console.{ConsoleElementView, ConsoleGridView, ConsoleHeadlineView, ConsoleView}
import blockudoku.windows.{FocusManager, Window}
import io.gitlab.freeeezee.yadis.ComponentContainer

import scala.util.{Failure, Success, Try}

class GameStateInstance extends Window {
  private val container = ComponentContainer().registerComponents().buildProvider()

  private val commandFactory = container.get[CommandFactory]
  private val commandInvoker = container.get[CommandInvoker]
  private val gridCollector = container.get[GridCollector]
  private val elementCollector = container.get[ElementCollector]
  private val focusManager = container.get[FocusManager]
  private val previewBuilder = container.get[GridPreviewBuilder]
  private val scoreCollector = container.get[blockudoku.controllers.ScoreCollector]
  private val serializer = container.get[blockudoku.saving.Serializer]

  private val consoleViews = initializeViews()

  private var colorIndex = scala.util.Random.nextInt(4)
  
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
  
  def getUniversalGridPreview: UniversalGridPreview = {
    val selectedElement = elementCollector.getElements
    val grid = gridCollector.getGrid
    UniversalGridPreview(selectedElement, grid)
  }

  def getElements: List[blockudoku.models.Element] = {
    elementCollector.getElements
  }
  
  def getSelectedElement: Option[blockudoku.models.Element] = {
    elementCollector.getSelectedElement
  }

  def selectElement(tileIndex: Int): Unit = {
    val element = elementCollector.getElements(tileIndex)
    val command = commandFactory.createSelectElementCommand(element)
    commandInvoker.execute(command)
  }

  def placeElement(tileIndex: Int): Unit = {
    val command = commandFactory.createSetElementCommand(
      elementCollector.getSelectedElement.get,
      tileIndex)
    commandInvoker.execute(command)
  }
  
  def toJson: String = {
    serializer.serialize()
  }
  
  def getColorIndex: Int = {
    colorIndex
  }

  def nextColorScheme() : Unit = {
    colorIndex = (colorIndex + 1) % 4
  }
  
  def prevColorScheme() : Unit = {
    colorIndex = (colorIndex - 1 + 4) % 4
  }

  def setColorScheme(ind: Int): Try[Unit] = {
    if (ind >=0 && ind < 4) {
      colorIndex = ind
      Success(())
    } else {
      Failure(new IllegalArgumentException("Color index out of bounds"))
    }
  }
  
  override def display(): Unit = {

  }

  override def setUpdated(): Unit = {

  }
}

object GameStateInstance {
  def fromJson(data: String): GameStateInstance = {
    val instance = new GameStateInstance()
    instance.serializer.deserialize(data)
    instance
  }
}

package services

import blockudoku.commands.{CommandFactory, CommandInvoker}
import blockudoku.controllers.GridCollector
import blockudoku.models.Grid
import blockudoku.services.GridPreviewBuilder
import blockudoku.windows.{FocusManager, Window}
import io.gitlab.freeeezee.yadis.ComponentContainer
import model.PlacementHistory
import util.registerComponents

import scala.util.{Failure, Success, Try, boundary}
import scala.util.boundary.break

class GameStateInstance(sessionKey: String) extends Window {
  private val container = ComponentContainer().registerComponents().buildProvider()

  private val sessionKeyStore = container.get[SessionKeyStore].setSessionKey(sessionKey)
  private val commandFactory = container.get[CommandFactory]
  private val commandInvoker = container.get[CommandInvoker]
  private val gridCollector = container.get[GridCollector]
  private val elementCollector = container.get[AdvancedElementManager]
  private val focusManager = container.get[FocusManager]
  private val previewBuilder = container.get[GridPreviewBuilder]
  private val scoreCollector = container.get[blockudoku.controllers.ScoreCollector]
  private val serializer = container.get[blockudoku.saving.Serializer]
  private var lastTimeUsed: Long = System.currentTimeMillis()
  private var placementHistory: List[PlacementHistory] = List()

  private var colorIndex = scala.util.Random.nextInt(4)

  def getLastTimeUsed: Long = {
    lastTimeUsed
  }
  
  def getUniversalGridPreviewGenerator: UniversalGridPreviewGenerator = {
    updateLastUsedTime()
    val selectedElement = elementCollector.getElements
    val grid = gridCollector.getGrid
    UniversalGridPreviewGenerator(selectedElement, grid)
  }

  def getElements: List[blockudoku.models.Element] = {
    elementCollector.getElements
  }

  private def selectElement(elementIndex: Int): Unit = {
    val element = elementCollector.elements(elementIndex)
    val command = commandFactory.createSelectElementCommand(element)
    commandInvoker.execute(command)
  }

  def getPlacementHistory: List[PlacementHistory] = {
    placementHistory
  }

  def updatePlacementHistory(placementHistories: List[PlacementHistory]): Unit = {
    updateLastUsedTime()

    if(verifyPlacementHistory(placementHistories)) {
      for (i <- this.placementHistory.length until placementHistories.length) {
        val ph = placementHistories(i)
        placeElement(ph.tileIndex, ph.elementIndex)
      }
      this.placementHistory = placementHistories
    }
  }

  private def verifyPlacementHistory(placementHistories: List[PlacementHistory]): Boolean = {
    boundary:
      if (placementHistories.isEmpty) break(false)
      if (placementHistories.length <= this.placementHistory.length) break(false)

      for(i <- this.placementHistory.indices) {
        if(placementHistories(i) != this.placementHistory(i)) {
          break(false)
        }
      }

      for(i <- placementHistories.indices.drop(1)) {
        if(placementHistories(i).placementIndex != placementHistories(i - 1).placementIndex + 1) {
          break(false)
        }
      }
      true
  }

  private def placeElement(tileIndex: Int, elementIndex: Int): Unit = {
    selectElement(elementIndex);
    val command = commandFactory.createSetElementCommand(
      elementCollector.getSelectedElement.get,
      tileIndex)
    commandInvoker.execute(command)
  }
  
  def toJson: String = {
    updateLastUsedTime()
    serializer.serialize()
  }
  
  def getColorIndex: Int = {
    colorIndex
  }

  def nextColorScheme() : Unit = {
    updateLastUsedTime()
    colorIndex = (colorIndex + 1) % 4
  }
  
  def prevColorScheme() : Unit = {
    updateLastUsedTime()
    colorIndex = (colorIndex - 1 + 4) % 4
  }

  def setColorScheme(ind: Int): Try[Unit] = {
    updateLastUsedTime()
    if (ind >=0 && ind < 4) {
      colorIndex = ind
      Success(())
    } else {
      Failure(new IllegalArgumentException("Color index out of bounds"))
    }
  }
  
  def getScore: Int = {
    scoreCollector.getScore
  }
  
  def getGrid: Grid = {
    gridCollector.getGrid
  }
  
  override def display(): Unit = {

  }

  override def setUpdated(): Unit = {

  }

  private def updateLastUsedTime(): Unit = {
    lastTimeUsed = System.currentTimeMillis()
  }
}

object GameStateInstance {
  def fromJson(data: String, sessionKey: String): GameStateInstance = {
    val instance = new GameStateInstance(sessionKey)
    instance.serializer.deserialize(data)
    instance
  }
}
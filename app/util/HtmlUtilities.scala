package util

import blockudoku.models.{Element, Tile}
import blockudoku.services.console.ElementFormatter
import blockudoku.models.TileState
import blockudoku.views.gui.ColorScheme

import javax.inject.Singleton

@Singleton
class HtmlUtilities() {
  def getTileButtonText(tile: Tile): String = {
    tile.state match {
      case TileState.empty => " "
      case TileState.blocked => "X"
      case TileState.previewValid => "*"
      case TileState.previewInvalid => "!"
    }
  }

  private val imagePathPrefix = "images"

  def getTileImage(tile: Tile): String = {
    if (tile.state == TileState.empty ) imagePathPrefix + "/background_block_final.png"
    else imagePathPrefix + ColorScheme.getColor(tile.colors)
  }

  def getTileMarkerImage(tile: Tile): Option[String] = {
    tile.state match {
      case TileState.empty => None
      case TileState.blocked => None
      case TileState.previewValid => Some(imagePathPrefix + "/block_green.png")
      case TileState.previewInvalid => Some(imagePathPrefix + "/block_red.png")
    }
  }

  def getElementAsText(element: Element): String = {
    val elementFormatter = ElementFormatter(element)
    val content = elementFormatter.content
    content.replaceAll("\n", "<br>")
  }
}

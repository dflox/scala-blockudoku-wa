package util

import blockudoku.models.{Element, Tile}
import blockudoku.services.console.ElementFormatter

import javax.inject.Singleton

@Singleton
class HtmlUtilities {
  def getTileButtonText(tile: Tile): String = {
    tile.state match {
      case blockudoku.models.TileState.empty => " "
      case blockudoku.models.TileState.blocked => "X"
      case blockudoku.models.TileState.previewValid => "*"
      case blockudoku.models.TileState.previewInvalid => "!"
    }
  }

  def getElementAsText(element: Element): String = {
    val elementFormatter = ElementFormatter(element)
    val content = elementFormatter.content
    content.replaceAll("\n", "<br>")
  }
}

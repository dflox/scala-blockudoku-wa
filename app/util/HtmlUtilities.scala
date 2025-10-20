package util

import blockudoku.models.Tile

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
}

package util

import blockudoku.models.{Grid, TileState}

extension (grid: Grid){
  def toUniqueString: String = {
    grid.tiles.map {
      case tile if tile.state == TileState.empty => "E" + tile.index
      case tile if tile.state == TileState.blocked => "B" + tile.index
      case tile if tile.state == TileState.previewValid => "V" + tile.index
      case tile if tile.state == TileState.previewInvalid => "I" + tile.index
      case _ => "X"
    }.mkString("-")
  }
}
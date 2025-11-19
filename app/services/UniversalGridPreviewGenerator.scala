package services

import blockudoku.models.TileState.{blocked, empty, previewInvalid, previewValid}
import blockudoku.models.{Element, Grid, Tile}
import model.UniversalGridPreview

import scala.collection.{immutable, mutable}
import scala.collection.mutable.Map


class UniversalGridPreviewGenerator(private val element: List[Element], private val grid: Grid) {

  def UniversalGridPreviewGenerator(element: List[Element], grid: Grid)
  : UniversalGridPreviewGenerator = {
    var normalizedGrid = grid.copyWithNewState(
      grid.tiles.filter(p => p.state == previewValid),
      empty)
    normalizedGrid = normalizedGrid.copyWithNewState(
      normalizedGrid.tiles.filter(p => p.state == previewInvalid),
      blocked)
    UniversalGridPreviewGenerator(element, normalizedGrid)
  }

  val yLength: Int = grid.yLength
  val xLength: Int = grid.xLength

  // Cache of element tile groups at positions. First key is element index, second key is
  // position index of the respective tile.
  private val elementTileGroups: mutable.Map[Int, mutable.Map[Int, List[Tile]]] =
    mutable.Map(element.indices.map(i => (i, mutable.Map[Int, List[Tile]]()))*)

  private def getElementTileMapAtPosition(xPos: Int, yPos: Int): immutable.Map[Int, List[Tile]] = {
    if (element.isEmpty) return immutable.Map()

    val baseTile = grid.tile(xPos, yPos)
    if (baseTile.isEmpty) return immutable.Map()

    val result = mutable.Map[Int, List[Tile]]()
    val pos = xPos + yPos * grid.xLength

    for(i <- element.indices) {
      val elem = element(i)
      if (!elementTileGroups(i).contains(pos)) {
        val elementTiles = grid.elementTiles(elem, pos)
          .map(l => l.drop(1))
          .getOrElse(List(baseTile.get))
        elementTileGroups(i)(pos) = elementTileGroups(i).getOrElse(pos, List()) ++ elementTiles
      }
      result += (i -> elementTileGroups(i)(pos))
    }
    result.toMap
  }

  def getUniversalGridPreview: UniversalGridPreview = {
    for {
      xPos <- 0 until xLength
      yPos <- 0 until yLength
    } yield {
      getElementTileMapAtPosition(xPos, yPos)
    }

    val elementTileMapAtPositions: immutable.Map[Int, immutable.Map[Int, List[Tile]]] =
      element.indices.map(i => (i, elementTileGroups(i).toMap)).toMap

    UniversalGridPreview(
      elementTileMapAtPositions,
      yLength,
      xLength
    )
  }

  def getValidTiles(xPos: Int, yPos: Int): immutable.Map[Int, List[Tile]] = {
    getElementTileMapAtPosition(xPos, yPos)
      .map( (i, tiles) => (i, tiles.filter(_.state != empty)) )
  }

  def getInvalidTiles(xPos: Int, yPos: Int): immutable.Map[Int, List[Tile]] = {
    getElementTileMapAtPosition(xPos, yPos).map( (i, tiles) => (i, tiles.filter(_.state == blocked)) )
  }

  def getTile(xPos: Int, yPos: Int): Option[Tile] = {
    grid.tile(xPos, yPos)
  }
}

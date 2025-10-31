package services

import blockudoku.models.TileState.{blocked, empty, previewInvalid, previewValid}
import blockudoku.models.{Element, Grid, Tile}

class UniversalGridPreview(private val element: Option[Element], private val grid: Grid) {

  def UniversalGridPreview(element: Option[Element], grid: Grid)
  : UniversalGridPreview = {
    var normalizedGrid = grid.copyWithNewState(
      grid.tiles.filter(p => p.state == previewValid),
      empty)
    normalizedGrid = normalizedGrid.copyWithNewState(
      normalizedGrid.tiles.filter(p => p.state == previewInvalid),
      blocked)
    new UniversalGridPreview(element, normalizedGrid)
  }

  val yLength: Int = grid.yLength
  val xLength: Int = grid.xLength

  private var elementTileGroups: Map[Int, List[Tile]] = Map()

  private def getElementTileGroupAtPosition(xPos: Int, yPos: Int): List[Tile] = {
    if (element.isEmpty) return List()
    val pos = xPos + yPos * grid.xLength
    if (!elementTileGroups.contains(pos)) {
      val elementTiles = grid.elementTiles(element.get, pos).getOrElse(List())
      elementTileGroups += (pos -> elementTiles)
    }
    elementTileGroups(pos)
  }

  def getValidTiles(xPos: Int, yPos: Int): List[Tile] = {
    getElementTileGroupAtPosition(xPos, yPos).filter(_.state == empty)
  }

  def getInvalidTiles(xPos: Int, yPos: Int): List[Tile] = {
    getElementTileGroupAtPosition(xPos, yPos).filter(_.state == blocked)
  }

  def getTile(xPos: Int, yPos: Int): Option[Tile] = {
    grid.tile(xPos, yPos)
  }

  def isElementSelected: Boolean = {
    element.isDefined
  }
}

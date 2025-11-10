package util

import blockudoku.models.{Element, Point}
import blockudoku.views.gui.ColorScheme

object ElementGenerator {
  private val maxElementLength: Int = 3

  def generateElement(slot: Int): Element = {
    var points = List[Point](Point(0, 0))
    val length = scala.util.Random.between(1, maxElementLength)
    val randomColor = scala.util.Random.nextInt(ColorScheme.current.length)

    for i <- 0 until length do
      points = generateNextPoint(points) :: points

    Element(points, slot, randomColor)
  }

  private def generateNextPoint(points: List[Point]): Point = {
    val possiblePoints = (0 to 7).toList
      .map(num => pointFromDirection(points.last, num))
      .filter(point => !points.contains(point))

    possiblePoints(scala.util.Random.nextInt(possiblePoints.length))
  }

  private def pointFromDirection(point: Point, direction: Int): Point = {
    direction match
      case 0 => Point(point.xPos + 1, point.yPos) // east
      case 1 => Point(point.xPos + 1, point.yPos - 1) // south-east
      case 2 => Point(point.xPos, point.yPos - 1) // south
      case 3 => Point(point.xPos - 1, point.yPos - 1) // south-west
      case 4 => Point(point.xPos - 1, point.yPos) // west
      case 5 => Point(point.xPos - 1, point.yPos + 1) // north-west
      case 6 => Point(point.xPos, point.yPos + 1) // north
      case 7 => Point(point.xPos + 1, point.yPos + 1) // north-east
  }
}

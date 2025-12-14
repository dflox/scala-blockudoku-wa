package util

import scala.util.boundary
import scala.util.boundary.break

class ProbabilityList[ItemType](private val items: Seq[(ItemType, Double)]) {
  private val totalWeight: Double = items.map(_._2).sum

  def isValid: Boolean = items.nonEmpty && totalWeight == 1.0

  def getRandomItem(randomValue: Double): ItemType = {
    var cumulativeWeight = 0.0

    boundary:
      items.foreach { item =>
        cumulativeWeight += item._2
        if randomValue <= cumulativeWeight then
          break(item._1)
      }
      items.last._1
  }
}

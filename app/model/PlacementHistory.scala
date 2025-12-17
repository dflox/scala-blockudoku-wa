package model

import play.api.libs.json.{Json, OWrites, Reads}

case class PlacementHistory(placementIndex: Int, elementIndex: Int, tileIndex: Int)

object PlacementHistory {

  implicit val elementPlacementWrites: OWrites[PlacementHistory] = OWrites { ep =>
    Json.obj(
      "placementIndex" -> ep.placementIndex,
      "elementIndex" -> ep.elementIndex,
      "tileIndex" -> ep.tileIndex
    )
  }
  implicit val elementPlacementReads: Reads[PlacementHistory] = Reads { json =>
    for {
      placementIndex <- (json \ "placementIndex").validate[Int]
      elementIndex <- (json \ "elementIndex").validate[Int]
      tileIndex <- (json \ "tileIndex").validate[Int]
    } yield PlacementHistory(placementIndex, elementIndex, tileIndex)
  }
}


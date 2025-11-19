package model

import play.api.libs.json.{Json, OWrites, Reads}

case class ElementPlacement(elementIndex: Int, positionIndex: Int)

object ElementPlacement {

  implicit val elementPlacementWrites: OWrites[ElementPlacement] = OWrites { ep =>
    Json.obj(
      "elementIndex" -> ep.elementIndex,
      "positionIndex" -> ep.positionIndex
    )
  }
  implicit val elementPlacementReads: Reads[ElementPlacement] = Reads { json =>
    for {
      elementIndex <- (json \ "elementIndex").validate[Int]
      positionIndex <- (json \ "positionIndex").validate[Int]
    } yield ElementPlacement(elementIndex, positionIndex)
  }
}

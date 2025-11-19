package model

import blockudoku.models.Element
import blockudoku.saving.serializerJSONImpl.ModelSerializer.*
import model.UniversalGridPreview.*
import play.api.libs.json.*
import play.api.libs.json.Format.GenericFormat

case class GameData(elements: List[Element], 
                    universalGridPreview: UniversalGridPreview,
                    score: Int,
                    colorIndex: Int)

case object GameData {
  implicit val gameDataWrites: OWrites[GameData] = OWrites { gd =>
    Json.obj(
      "elements" -> Json.toJson(gd.elements),
      "universalGridPreview" -> Json.toJson(gd.universalGridPreview),
      "score" -> gd.score,
      "colorIndex" -> gd.colorIndex
    )
  }
  implicit val gameDataReads: Reads[GameData] = Reads { json =>
    for {
      elements <- (json \ "elements").validate[List[Element]]
      universalGridPreview <- (json \ "universalGridPreview").validate[UniversalGridPreview]
      score <- (json \ "score").validate[Int]
      colorIndex <- (json \ "colorIndex").validate[Int]
    } yield GameData(elements, universalGridPreview, score, colorIndex)
  }
} 

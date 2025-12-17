package model

import blockudoku.models.{Element, Grid}
import blockudoku.saving.serializerJSONImpl.ModelSerializer.*
import model.UniversalGridPreview.*
import play.api.libs.json.*
import play.api.libs.json.Format.GenericFormat

case class GameData(elements: List[Element], 
                    universalGridPreview: UniversalGridPreview,
                    grid: Grid,
                    score: Int,
                    colorIndex: Int,
                    sessionId: String,
                    placementHistory: List[PlacementHistory]
                   )

case object GameData {
  implicit val gameDataWrites: OWrites[GameData] = OWrites { gd =>
    Json.obj(
      "grid" -> Json.toJson(gd.grid),
      "elements" -> Json.toJson(gd.elements),
      "universalGridPreview" -> Json.toJson(gd.universalGridPreview),
      "score" -> gd.score,
      "colorIndex" -> gd.colorIndex,
      "sessionId" -> gd.sessionId,
      "placementHistory" -> Json.toJson(gd.placementHistory)
    )
  }
  implicit val gameDataReads: Reads[GameData] = Reads { json =>
    for {
      grid <- (json \ "grid").validate[Grid]
      elements <- (json \ "elements").validate[List[Element]]
      universalGridPreview <- (json \ "universalGridPreview").validate[UniversalGridPreview]
      score <- (json \ "score").validate[Int]
      colorIndex <- (json \ "colorIndex").validate[Int]
      sessionId <- (json \ "sessionId").validate[String]
      placementHistory <- (json \ "placementHistory").validate[List[PlacementHistory]]
    } yield GameData(elements, universalGridPreview, grid, score, colorIndex, sessionId, placementHistory)
  }
} 

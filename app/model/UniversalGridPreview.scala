package model

import blockudoku.models.Tile
import blockudoku.saving.serializerJSONImpl.ModelSerializer.*
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{Json, OWrites, Reads, Writes}

case class UniversalGridPreview(elementTileGroups: Map[Int, Map[Int, List[Tile]]],
                                yLength: Int,
                                xLength: Int)

object UniversalGridPreview {
  implicit val mapIntListTileWrites: Writes[Map[Int, List[Tile]]] = Writes { map =>
    Json.obj(map.map { case (k, v) => k.toString -> Json.toJsFieldJsValueWrapper(v) }.toSeq*)
  }
  implicit val mapIntListTileReads: Reads[Map[Int, List[Tile]]] = Reads { json =>
    json.validate[Map[String, List[Tile]]].map(_.map { case (k, v) => k.toInt -> v })
  }

  implicit val elementTileGroupsWrites: Writes[Map[Int, Map[Int, List[Tile]]]] = Writes { map =>
    Json.obj(map.map { case (k, v) => k.toString -> Json.toJsFieldJsValueWrapper(v)(mapIntListTileWrites) }.toSeq*)
  }
  implicit val elementTileGroupsReads: Reads[Map[Int, Map[Int, List[Tile]]]] = Reads { json =>
    json.validate[Map[String, Map[Int, List[Tile]]]](Reads.mapReads(mapIntListTileReads)).map(_.map { case (k, v) => k.toInt -> v })
  }

  implicit val universalGridPreviewWrites: OWrites[UniversalGridPreview] = OWrites { ugp =>
    Json.obj(
      "elementTileGroups" -> Json.toJson(ugp.elementTileGroups)(elementTileGroupsWrites),
      "yLength" -> ugp.yLength,
      "xLength" -> ugp.xLength
    )
  }
  implicit val universalGridPreviewReads: Reads[UniversalGridPreview] = Reads { json =>
    for {
      elementTileGroups <- (json \ "elementTileGroups").validate[Map[Int, Map[Int, List[Tile]]]](elementTileGroupsReads)
      yLength <- (json \ "yLength").validate[Int]
      xLength <- (json \ "xLength").validate[Int]
    } yield UniversalGridPreview(elementTileGroups, yLength, xLength)
  }
}
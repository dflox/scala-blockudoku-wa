package model

import play.api.libs.json.{Json, OWrites, Reads}

case class SimpleUser(username: String, password: String)

object SimpleUser {
  implicit val simpleUserWrites: OWrites[SimpleUser] = OWrites { su =>
    Json.obj(
      "username" -> su.username,
      "password" -> su.password
    )
  }

  implicit val simpleUserReads: Reads[SimpleUser] = Reads { json =>
    for {
      username <- (json \ "username").validate[String]
      password <- (json \ "password").validate[String]
    } yield SimpleUser(username, password)
  }
}

package model

import org.pac4j.core.profile.UserProfile
import play.api.libs.json.{Json, OWrites, Reads}

case class UserInfo(username: String)

object UserInfo {
  def fromProfile(profile: UserProfile): UserInfo = {
    UserInfo(
      username = profile.getUsername
    )
  }

  implicit val simpleUserWrites: OWrites[UserInfo] = OWrites { ui =>
    Json.obj(
      "username" -> ui.username
    )
  }

  implicit val simpleUserReads: Reads[UserInfo] = Reads { json =>
    for {
      username <- (json \ "username").validate[String]
    } yield UserInfo(username)
  }
}
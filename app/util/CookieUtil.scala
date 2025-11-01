package util

import controllers.COOKIE_KEY
import play.api.mvc.{AnyContent, Cookie, Request, Result}

extension (result: Result) {
  def withGameStateKeyCookie(key: String): Result =
    result.withCookies(Cookie(COOKIE_KEY, key))
}

def getStateKeyCookie(implicit request: Request[?]): Option[String] = request
  .cookies
  .get(COOKIE_KEY) match {
  case Some(cookie) => Some(cookie.value)
  case None => None
}
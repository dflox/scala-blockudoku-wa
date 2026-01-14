package util

import controllers.{COOKIE_KEY, TOKEN_KEY}
import play.api.mvc.{AnyContent, Cookie, Request, Result}

extension (result: Result) {
  def withGameStateKeyCookie(key: String): Result =
    result.withCookies(Cookie(COOKIE_KEY, key, httpOnly = false))
    
  def withJwtCookie(token: String): Result =
    result.withCookies(Cookie(TOKEN_KEY, token, httpOnly = true))
}

def getStateKeyCookie(implicit request: Request[?]): Option[String] = request
  .cookies
  .get(COOKIE_KEY) match {
  case Some(cookie) => Some(cookie.value)
  case None => None
}

def getJwtCookie(implicit request: Request[?]): Option[String] = request
  .cookies
  .get(TOKEN_KEY) match {
  case Some(cookie) => Some(cookie.value)
  case None => None
}
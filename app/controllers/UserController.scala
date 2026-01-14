package controllers

import model.{SimpleUser, UserInfo}
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Result}
import services.{HighScoreService, UserService}
import util.*

import javax.inject.{Inject, Singleton}

@Singleton
class UserController @Inject()(
                                val controllerComponents: SecurityComponents,
                                val jwtGenerator: JwtGenerator,
                                val userService: UserService,
                                val highScoreService: HighScoreService,
                                val configuration: Configuration
                              ) extends Security {

  private val clientUrl = configuration.get[String]("client.url")

  def callbackRedirect: Action[AnyContent] = Secure { implicit request =>
    val profile = request.profiles.head
    val jwt = jwtGenerator.generate(profile)
    Redirect(clientUrl).withJwtCookie(jwt)
  }

  def registerUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[SimpleUser].fold(
      error => {
        scala.concurrent.Future.successful(BadRequest("Invalid data. " + error.toString))
      },
      userData => {
        scala.concurrent.Future.successful(handleUserRegistration(userData))
      }
    )
  }

  private def handleUserRegistration(userData: SimpleUser): Result = {
    userService.findByUsername(userData.username) match {
      case Some(userId) =>
        Conflict("User already exists.")
      case None =>
        userService.addUser(userData)
        Created("User registered successfully.")
    }
  }

  def getUserInfo: Action[AnyContent] = Secure("CookieClient") { implicit request =>
    request.profiles.headOption match {
      case Some(user) =>
        Ok(Json.toJson(UserInfo.fromProfile(user)))
      case None =>
        Unauthorized("No profile found")
    }
  }
}

//  def basicAuth: Action[AnyContent] = Secure("BasicAuthClient") { implicit request =>
//    val ctx = request.webContext
//    val profileManager = new ProfileManager(ctx)
//
//    val profile =
//      profileManager.get(true).get().asInstanceOf[CommonProfile]
//
//    profiles.headOption match {
//      case Some(user) =>
//        val jwt = jwtGenerator.generate(user)
//        Ok(s"Hello, ${user.getId}! You have successfully logged in using Basic Authentication.")
//          .withJwtCookie(jwt)
//      case None =>
//        Unauthorized("No profile found")
//    }
//  }
//
//  def getHighScore: Action[AnyContent] = Secure("BasicAuthClient") {
//    implicit request =>
//      profiles.headOption match {
//        case Some(user) =>
//          Ok(s"High score data for user: ${user.getId}")
//        case None =>
//          Unauthorized("No profile found")
//      }
//  }

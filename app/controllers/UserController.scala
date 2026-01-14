package controllers

import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.mvc.{Action, AnyContent, Request}

import javax.inject.{Inject, Singleton}
import services.{HighScoreService, UserService}
import util.*
import play.api.Configuration

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

  def registerUser: Action[AnyContent] = Action { implicit request =>
    val maybeUser = for {
      username <- request.body.asFormUrlEncoded.flatMap(_.get("username").flatMap(_.headOption))
      password <- request.body.asFormUrlEncoded.flatMap(_.get("password").flatMap(_.headOption))
    } yield (username, password)
    maybeUser match {
      case Some((username, password)) =>
        userService.findByUsername(username) match {
          case Some(_) =>
            Conflict("User already exists")
          case None =>
            userService.addUser(username, password)
            Created("User registered successfully")
        }
      case None =>
        BadRequest("Missing username or password")
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

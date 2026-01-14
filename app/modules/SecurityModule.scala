package modules

import com.google.inject.{AbstractModule, Provides}
import controllers.TOKEN_KEY
import org.pac4j.core.config.Config
import org.pac4j.core.context.{CallContext, FrameworkParameters}
import org.pac4j.core.context.session.{SessionStore, SessionStoreFactory}
import org.pac4j.core.credentials.{Credentials, UsernamePasswordCredentials}
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.profile.CommonProfile
import org.pac4j.http.client.direct.CookieClient
import org.pac4j.http.client.indirect.FormClient
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import org.pac4j.jwt.profile.JwtGenerator
import org.pac4j.oauth.client.{GitHubClient, Google2Client}
import org.pac4j.play.scala.{DefaultSecurityComponents, SecurityComponents}
import org.pac4j.play.store.{PlayCookieSessionStore, ShiroAesDataEncrypter}
import org.pac4j.play.{CallbackController, LogoutController}
import play.api.{Configuration, Environment}
import services.UserService

import java.nio.charset.StandardCharsets
import java.util.Optional

class SecurityModule(environment: Environment, configuration: Configuration)
  extends AbstractModule {

  override def configure(): Unit = {
    val sKey = configuration.get[String]("play.http.secret.key").substring(0, 16)
    val dataEncrypter = new ShiroAesDataEncrypter(sKey.getBytes(StandardCharsets.UTF_8))
    val playSessionStore = new PlayCookieSessionStore(dataEncrypter)
    bind(classOf[SessionStore]).toInstance(playSessionStore)
    bind(classOf[SecurityComponents]).to(classOf[DefaultSecurityComponents])

    // Explicitly bind the controllers pac4j provides
    bind(classOf[CallbackController]).asEagerSingleton()
    bind(classOf[LogoutController]).asEagerSingleton()
    bind(classOf[JwtGenerator]).toInstance(provideJwtGenerator())
  }
  
  private def provideJwtGenerator(): JwtGenerator = {
    val jwtSecret = configuration.get[String]("jwt.secret")
    val signatureConfig =
      new SecretSignatureConfiguration(jwtSecret)
    new JwtGenerator(signatureConfig)
  }

  @Provides
  def provideBasicAuthenticator(userService: UserService): Authenticator = new Authenticator {
          override def validate(ctx: CallContext, credentials: Credentials)
          : Optional[Credentials] = {
            val upc = credentials.asInstanceOf[UsernamePasswordCredentials]
            val userOpt = userService.findByUsername(upc.getUsername)

            userOpt match {
              case Some(user) if user.password == upc.getPassword =>
                val profile = new CommonProfile()
                profile.setId(user.username)
                profile.addAttribute("username", user.username)
                upc.setUserProfile(profile)
                Optional.of(upc)
              case _ =>
                throw new org.pac4j.core.exception.CredentialsException("Invalid credentials")
            }
          }
        }
  
  @Provides
  def provideConfig(basicAuthenticator: Authenticator, sessionStore: SessionStore): Config = {

    // 1. Define the Authenticator (Validate username/password)
    //    val basicAuthenticator = new Authenticator {
    //      override def validate(ctx: CallContext, credentials: Credentials)
    //      : Optional[Credentials] = {
    //        val upc = credentials.asInstanceOf[UsernamePasswordCredentials]
    //        // Blocking here is necessary as pac4j validate is synchronous, but your DB is async.
    //        // In real apps, use a dedicated execution context or synchronous DB driver if
    //        strictly needed.
    //        val userOpt = userService.findByUsername(upc.getUsername)
    //
    //        userOpt match {
    //          case Some(user) if user.password == upc.getPassword =>
    //            val profile = new CommonProfile()
    //            profile.setId(user.username)
    //            profile.addAttribute("username", user.username)
    //            upc.setUserProfile(profile)
    //            Optional.of(upc)
    //          case _ =>
    //            throw new org.pac4j.core.exception.CredentialsException("Invalid credentials")
    //        }
    //      }
    //    }

    // 2. Define the Client (DirectBasicAuthClient checks the 'Authorization: Basic ...' header)
    //    val basicAuthClient = new DirectBasicAuthClient(basicAuthenticator)
    //    basicAuthClient.setName("BasicAuthClient")

    // 3. Register Clients (You can add Google/GitHub clients here later)

    val jwtSecret = configuration.get[String]("jwt.secret")
    val googleClientId = configuration.get[String]("google.clientId")
    val googleSecret = configuration.get[String]("google.secret")
    val githubClientId = configuration.get[String]("github.clientId")
    val githubSecret = configuration.get[String]("github.secret")
    val apiUrl = configuration.get[String]("api.url")

    // 1. Indirect Clients (Interactive/Browser-based)
    val googleClient = new Google2Client(googleClientId, googleSecret)
    val githubClient = new GitHubClient(githubClientId, githubSecret)

    
    val formClient = new FormClient("/login", basicAuthenticator)

    // 2. Direct Clients (Non-interactive/API-based)
    val signatureConfig = new SecretSignatureConfiguration(jwtSecret)
    val jwtAuthenticator = new JwtAuthenticator()
    jwtAuthenticator.addSignatureConfiguration(signatureConfig)

    val jwtClient = new CookieClient(TOKEN_KEY, jwtAuthenticator)
    
    
    // 3. Group them in the Config
    // The first argument is the default callback URL
    val config = new Config(
      "http://"+ apiUrl +"/callback",
      googleClient,
      githubClient,
      jwtClient
    )

    config.setSessionStoreFactory((_: FrameworkParameters) => sessionStore)
    config
  }
}
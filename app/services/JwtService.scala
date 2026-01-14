package services

import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration
import org.pac4j.jwt.profile.JwtGenerator
import play.api.Configuration

import javax.inject.Singleton

@Singleton
class JwtService(configuration: Configuration) {
  private val jwtSecret = configuration.get[String]("jwt.secret")

  private val signatureConfig =
    new SecretSignatureConfiguration(jwtSecret)

  private val encryptionConfig = new SecretEncryptionConfiguration(jwtSecret)

  val generator = new JwtGenerator(signatureConfig, encryptionConfig)
}

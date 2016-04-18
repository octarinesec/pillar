package de.kaufhof.pillar.config

import com.datastax.driver.core.{AuthProvider, PlainTextAuthProvider, SSLOptions}
import com.google.common.base.Strings
import com.typesafe.config.{Config, ConfigFactory}

import scala.language.implicitConversions

/**
  * Configuration for connection to cassandra.
  */
class ConnectionConfiguration(dataStoreName: String, environment: String, appConfig: Config) {

  val connectionConfig: Config = appConfig
    .getConfig(s"pillar.$dataStoreName.$environment")
    .withFallback(ConfigFactory.load("cassandraConnectionReference.conf"))

  val keyspace = connectionConfig.getString("cassandra-keyspace-name")
  val seedAddress = connectionConfig.getString("cassandra-seed-address")
  val port = connectionConfig.getInt("cassandra-port")

  val useSsl = connectionConfig.getBoolean("use-ssl")

  import ConfigHelper.toOptionalConfig

  val auth = Auth(connectionConfig.getOptionalConfig("auth"))

  val sslOptions: Option[SSLOptions] = SslConfig(connectionConfig.getOptionalConfig("ssl-options"))

}

class OptionalConfig(config: Config) {
  def getOptionalConfig(path: String): Option[Config] = {
    if (config.hasPath(path)) {
      Some(config.getConfig(path))
    } else {
      None
    }
  }
}

object ConfigHelper {
  implicit def toOptionalConfig(config: Config): OptionalConfig = {
    new OptionalConfig(config)
  }
}

abstract sealed class Auth

case class PlaintextAuth(username: String, password: String) extends Auth

object Auth {
  def apply(config: Option[Config]): Option[AuthProvider] = {
    config.map(config => new PlainTextAuthProvider(config.getString("username"), config.getString("password")))
  }
}

object SslConfig {
  def apply(config: Option[Config]): Option[SSLOptions] = {
    config.map(config => {
      val optionsBuilder = new SslOptionsBuilder()
      val trustStoreFile = config.getString("trust-store-path")
      val trustStorePassword = config.getString("trust-store-password")
      val trustStoreType = config.getString("trust-store-type")
      if (!Strings.isNullOrEmpty(trustStoreFile) && !Strings.isNullOrEmpty(trustStorePassword)) {
        optionsBuilder.withKeyStore(trustStoreFile, trustStorePassword, trustStoreType)
      }
      optionsBuilder.withSslContext()
      optionsBuilder.withTrustManager()
      optionsBuilder.build()
    })
  }
}
package de.kaufhof.pillar.config

import com.datastax.driver.core.{AuthProvider, PlainTextAuthProvider, SSLOptions}
import com.google.common.base.Strings
import com.typesafe.config.{Config, ConfigFactory}

/**
  * Configuration for connection to cassandra.
  */
class ConnectionConfiguration(dataStoreName: String, environment: String, appConfig: Config) {

  val connectionConfig = appConfig
    .atPath(s"pillar.$dataStoreName.$environment")
    .withFallback(ConfigFactory.load("cassandraConnectionReference.conf"))

  val keyspace = connectionConfig.getString("cassandra-keyspace-name")
  val seedAddress = connectionConfig.getString("cassandra-seed-address")
  val port = connectionConfig.getInt("cassandra-port")

  val useSsl = connectionConfig.getBoolean("use-ssl")

  val auth = Auth(connectionConfig.atPath("auth"))

  val sslOptions: Option[SSLOptions] = SslConfig(connectionConfig.atPath("ssl-options"))

}

abstract sealed class Auth

case class PlaintextAuth(username: String, password: String) extends Auth

object Auth {
  def apply(config: Config): Option[AuthProvider] = {
    if (config.isEmpty) {
      None
    } else {
      Some(new PlainTextAuthProvider(config.getString("username"), config.getString("password")))
    }
  }
}

object SslConfig {
  def apply(config: Config): Option[SSLOptions] = {
    if (config.isEmpty) {
      None
    } else {
      val optionsBuilder = new SslOptionsBuilder()
      val trustStoreFile = config.getString("trust-store-path")
      val trustStorePassword = config.getString("trust-store-password")
      val trustStoreType = config.getString("trust-store-type")
      if (!Strings.isNullOrEmpty(trustStoreFile) && !Strings.isNullOrEmpty(trustStorePassword)) {
        optionsBuilder.withKeyStore(trustStoreFile, trustStorePassword, trustStoreType)
      }
      optionsBuilder.withSslContext()
      optionsBuilder.withTrustManager()
      Some(optionsBuilder.build())
    }
  }
}
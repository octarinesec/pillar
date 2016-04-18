package de.kaufhof.pillar.config

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

/**
  * //TODO Document me.
  */
class ConnectionConfigurationTest extends FunSpec with BeforeAndAfter with Matchers {
  describe("#initialze") {
    it("should show defaults for useSsl") {
      val config: Config = ConfigFactory.load()
      val configuration: ConnectionConfiguration = new ConnectionConfiguration("faker", "development", config)
      configuration.useSsl === false
    }

  }
}

package de.kaufhof.pillar.cli

import de.kaufhof.pillar.Registry
import com.datastax.driver.core.Session

case class Command(action: MigratorAction, session: Session, keyspace: String, timeStampOption: Option[Long], registry: Registry)
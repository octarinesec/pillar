package de.kaufhof.pillar

import java.util.Date

import com.datastax.driver.core.Session

object Migrator {
  def apply(registry: Registry, appliedMigrationsTableName: String): Migrator = {
    new CassandraMigrator(registry, appliedMigrationsTableName)
  }

  def apply(registry: Registry, reporter: Reporter, appliedMigrationsTableName: String): Migrator = {
    new ReportingMigrator(reporter, apply(registry, appliedMigrationsTableName), appliedMigrationsTableName)
  }
}

trait Migrator {
  def migrate(session: Session, dateRestriction: Option[Date] = None)

  def initialize(session: Session, keyspace: String, replicationStrategy: ReplicationStrategy)

  def createKeyspace(session: Session, keyspace: String, replicationStrategy: ReplicationStrategy)

  def createMigrationsTable(session: Session, keyspace: String)

  def destroy(session: Session, keyspace: String)
}

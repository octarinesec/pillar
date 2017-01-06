package de.kaufhof.pillar

import java.util.Date

import com.datastax.driver.core.Session

trait Reporter {
  def migrating(session: Session, dateRestriction: Option[Date])
  def applying(migration: Migration)
  def reversing(migration: Migration)
  def destroying(session: Session, keyspace: String)
  def creatingKeyspace(session: Session, keyspace: String, replicationStrategy: ReplicationStrategy)
  def creatingMigrationsTable(session: Session, keyspace: String, appliedMigrationsTableName: String)
}

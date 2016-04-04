package de.kaufhof.pillar

class InvalidMigrationException(val errors: Map[String,String]) extends RuntimeException

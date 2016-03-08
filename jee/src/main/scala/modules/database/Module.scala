package modules.database

object Module extends modules.Module {
  override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {
    val dataSourceDefinition = factory.getObjectByName("DataSourceDefinition").asInstanceOf[DataSourceDefinition]

    val flyway = new org.flywaydb.core.Flyway()
    flyway.setValidateOnMigrate(false)
    flyway.setDataSource(dataSourceDefinition.url, dataSourceDefinition.user, dataSourceDefinition.password)
    flyway.migrate()

    scalikejdbc.GlobalSettings.loggingSQLAndTime = scalikejdbc.LoggingSQLAndTimeSettings(
      enabled = true,
      singleLineMode = true,
      logLevel = 'DEBUG
    )

    scalikejdbc.ConnectionPool.singleton(dataSourceDefinition.url, dataSourceDefinition.user, dataSourceDefinition.password, dataSourceDefinition.connectionPoolSettings)
    repl.processLine("import scalikejdbc._")
    repl.processLine("implicit val dbsession = AutoSession")
  }
}

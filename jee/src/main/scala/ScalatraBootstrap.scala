class ScalatraBootstrap extends org.scalatra.LifeCycle {
  val projectName = buildinfo.BuildInfo.name

  private def getDataSource:javax.sql.DataSource = {
    try {
      new javax.naming.InitialContext().lookup(
        "java:comp/env/jdbc/%s".format(projectName)
      ).asInstanceOf[javax.sql.DataSource]
    }
    catch {
      case e:javax.naming.NameNotFoundException =>
        val dataSource = new org.h2.jdbcx.JdbcDataSource()
        dataSource.setURL("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MODE=MySQL;INIT=CREATE SCHEMA IF NOT EXISTS \"public\"".format(projectName))
        dataSource
    }
  }

  private def initDatabase:Unit = {
    if (!scalikejdbc.ConnectionPool.isInitialized()) {
      val flyway = new org.flywaydb.core.Flyway()
      val dataSource = getDataSource
      flyway.setDataSource(dataSource)
      flyway.migrate()
      scalikejdbc.ConnectionPool.singleton(new scalikejdbc.DataSourceConnectionPool(dataSource))
    }
  }

  override def init(context: javax.servlet.ServletContext) {
    initDatabase
    //context.mount(classOf[some.Class], "/some/path/*")
  }
}

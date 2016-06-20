package db.migration

import scalikejdbc.DBSession

trait ScalikeJdbcMigration extends org.flywaydb.core.api.migration.jdbc.JdbcMigration with scalikejdbc.SQLInterpolation {
  override def migrate(con:java.sql.Connection):Unit = {
    migrate(new DBSession {
      override val connectionAttributes = scalikejdbc.DBConnectionAttributes()
      override val isReadOnly: Boolean = false
      override val conn = con
    })
  }

  def migrate(implicit session:DBSession)
}

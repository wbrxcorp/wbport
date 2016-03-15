package profiles.default

object DataSourceDefinition extends modules.database.DataSourceDefinition {
  private val projectName = buildinfo.BuildInfo.name

  def url:String = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MODE=MySQL;INIT=CREATE SCHEMA IF NOT EXISTS \"public\"".format(projectName)
  def user:String = "sa"
  def password:String = ""
}

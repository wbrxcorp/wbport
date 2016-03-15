package profiles.wbport.production

object DataSourceDefinition extends modules.database.DataSourceDefinition {
  private val projectName = buildinfo.BuildInfo.name

  def url:String = "jdbc:h2:/var/lib/tomcat7/db/wbport;MVCC=true;;AUTO_SERVER=true;DB_CLOSE_DELAY=-1"
  def user:String = "sa"
  def password:String = ""
}

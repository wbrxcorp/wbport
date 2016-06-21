package profiles.migration

object DataSourceDefinition extends modules.database.DataSourceDefinition {
  def url:String = "jdbc:h2:/var/lib/tomcat7/db/wbport;MVCC=true;;AUTO_SERVER=true;DB_CLOSE_DELAY=-1"
  def user:String = "sa"
  def password:String = ""
}

object DstDataSourceDefinition extends modules.database.MySQLDataSourceDefinition {
  private val projectName = buildinfo.BuildInfo.name

  override def hostname:String = "localhost"
  override def dbname:String = projectName
  def user:String = projectName
  def password:String = ""
  override def timezone:Option[String] = Some("Asia/Tokyo")
}

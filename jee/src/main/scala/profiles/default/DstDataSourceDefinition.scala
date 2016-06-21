package profiles.default

object DstDataSourceDefinition extends modules.database.MySQLDataSourceDefinition {
  private val projectName = buildinfo.BuildInfo.name

  override def hostname:String = "localhost"
  override def dbname:String = projectName
  def user:String = projectName
  def password:String = ""
  override def timezone:Option[String] = Some("Asia/Tokyo")
}

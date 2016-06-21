package modules.database

trait DataSourceDefinition {
  def url:String
  def user:String
  def password:String
  def connectionPoolSettings:scalikejdbc.ConnectionPoolSettings = scalikejdbc.ConnectionPoolSettings()
}

trait MySQLDataSourceDefinition extends DataSourceDefinition {
  def url:String = {
    "jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&useCompression=true&autoReconnect=true&socketTimeout=10000".format(hostname, dbname) + (timezone match {
      case Some(timezone) => "&useLegacyDatetimeCode=false&serverTimezone=%s".format(timezone)
      case None => ""
    })
  }
  def hostname:String = "localhost"
  def dbname:String
  def timezone:Option[String] = None
}

package modules.database

trait DataSourceDefinition {
  def url:String
  def user:String
  def password:String
  def connectionPoolSettings:scalikejdbc.ConnectionPoolSettings = scalikejdbc.ConnectionPoolSettings()
}

package profiles.default

object DataSourceDefinition extends modules.database.DataSourceDefinition {
  def url:String = "jdbc:h2:./db/wbport;MVCC=true;;AUTO_SERVER=true;DB_CLOSE_DELAY=-1"
  def user:String = "sa"
  def password:String = ""
}

package profiles.default

object DataSourceDefinition extends modules.database.DataSourceDefinition {
  def url:String = "jdbc:h2:mem:playground;DB_CLOSE_DELAY=-1"
  def user:String = "sa"
  def password:String = ""
}

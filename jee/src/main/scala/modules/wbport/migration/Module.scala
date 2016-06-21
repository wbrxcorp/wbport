package modules.wbport.migration

import scalikejdbc.{DB,NamedDB}

object Module extends modules.Module with scalikejdbc.SQLInterpolation {
  override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {
    // migrationでのコピー先データソース
    val dstDataSourceDefinition = factory.getObjectByName("DstDataSourceDefinition").asInstanceOf[modules.database.DataSourceDefinition]
    val flyway = new org.flywaydb.core.Flyway()
    flyway.setValidateOnMigrate(false)
    flyway.setDataSource(dstDataSourceDefinition.url, dstDataSourceDefinition.user, dstDataSourceDefinition.password)
    flyway.migrate()

    scalikejdbc.ConnectionPool.add("dst", dstDataSourceDefinition.url, dstDataSourceDefinition.user, dstDataSourceDefinition.password, dstDataSourceDefinition.connectionPoolSettings)
  }

  override def dependsOn:Seq[String] = Seq("database")

  def migrate():Unit = {
    val sqlUpdates = DB readOnly { implicit srcSession =>
      Seq(sql"delete from servers".update) ++
      Seq(sql"delete from users".update) ++
      sql"select * from users".map { row =>
        sql"insert into users(id,email,password,nickname,auth_token,auth_token_expires_at,admin_user,created_at,updated_at) values(${row.long(1)},${row.string(2)},${row.stringOpt(3)},${row.stringOpt(4)},${row.string(5)},${row.string(6)},${row.boolean(7)},${row.jodaDateTime(8)},${row.jodaDateTime(9)})".update
      }.list.apply ++
      sql"select * from servers".map { row =>
        sql"insert into servers(id,fqdn,user_id) values(${row.long(1)},${row.string(2)},${row.long(3)})".update
      }.list.apply ++
      Seq(sql"delete from domains".update) ++
      sql"select * from domains".map { row =>
        sql"insert into domains(domain_name,user_id) values(${row.string(1)},${row.longOpt(2)})".update
      }.list.apply ++
      Seq(sql"delete from system_config".update) ++
      sql"select * from system_config".map { row =>
        sql"insert into system_config(config_key,config_value,created_at,updated_at) values(${row.string(1)},${row.string(2)},${row.jodaDateTime(3)},${row.jodaDateTime(4)})".update
      }.list.apply ++
      Seq(sql"delete from mail_log".update) ++
      sql"select * from mail_log".map { row =>
        sql"insert into mail_log(id,recipient,subject,success,error_message,created_at) values(${row.long(1)},${row.string(2)},${row.stringOpt(3)},${row.boolean(4)},${row.stringOpt(5)},${row.jodaDateTime(6)})".update
      }.list.apply
    }

    NamedDB("dst") localTx { implicit dstSession => sqlUpdates.foreach(_.apply) }

    println("migrated")
  }
}

package db.migration

import com.walbrix.flyway.{DBSession, ScalikeJdbcMigration}
import scalikejdbc._
/**
 * Created by shimarin on 14/11/12.
 */
class V9999__Testdata extends ScalikeJdbcMigration {
  override def migrate(implicit session: DBSession): Unit = {
    val email = "user@example.com"
    val password = com.walbrix.encryptPassword("secret")
    sql"""insert into users(email,password,auth_token,auth_token_expires_at,admin_user)
       values(${email}, ${password}, 'AUTHTOKEN', DATEADD('YEAR', 1, now()), true)""".update().apply()
    val userId = sql"select last_insert_id()".map(_.int(1)).single().apply().get
    sql"insert into domains(domain_name,user_id) values('wbport.com',null)".update().apply()
    sql"insert into domains(domain_name,user_id) values('walbrix.com',${userId})".update().apply()
    sql"insert into servers(fqdn,user_id) values('test.wbport.com',${userId})".update().apply()
  }
}

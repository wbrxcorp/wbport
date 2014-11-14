package db.migration

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration
import org.springframework.jdbc.core.JdbcTemplate

/**
 * Created by shimarin on 14/11/12.
 */
class V9999__Testdata extends SpringJdbcMigration {
  override def migrate(jdbcTemplate: JdbcTemplate): Unit = {
    jdbcTemplate.update("insert into users(email,password,auth_token,auth_token_expires_at,admin_user) values('user@example.com', '0PiNI$e243e97898f551b4cf056358201bd50b8fe87c89884ee93814bde5e29b3c2d81', 'AUTHTOKEN', DATEADD('YEAR', 1, now()), true)")
    val userId = jdbcTemplate.queryForObject("select last_insert_id()", classOf[Integer])
    jdbcTemplate.update("insert into domains(domain_name,user_id) values(?,null)", "wbport.com")
    jdbcTemplate.update("insert into domains(domain_name,user_id) values(?,1)", "walbrix.com")
    jdbcTemplate.update("insert into servers(fqdn,user_id) values(?,?)", "test.wbport.com", userId)
  }
}

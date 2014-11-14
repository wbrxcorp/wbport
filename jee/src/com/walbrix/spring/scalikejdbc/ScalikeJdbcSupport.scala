package com.walbrix.spring.scalikejdbc

import java.sql.Connection
import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.datasource.DataSourceUtils

/**
 * Created by shimarin on 14/11/02.
 */

class TransactionAwareDBSession(private val dataSource:DataSource,
                                override val isReadOnly:Boolean = false) extends scalikejdbc.DBSession {
  override val conn:Connection = DataSourceUtils.getConnection(dataSource)
  override def close(): Unit = {
    util.control.Exception.ignoring(classOf[Throwable]) {
      DataSourceUtils.releaseConnection(conn, dataSource)
    }
  }
}

trait ScalikeJdbcSupport {
  @Autowired private var dataSource:DataSource = _
  implicit def __scalikejdbc_session = new TransactionAwareDBSession(dataSource, false)
  def readOnlySession = new TransactionAwareDBSession(dataSource, true)
}
package com.wbport

import java.security.MessageDigest

import com.walbrix.spring.scalikejdbc.ScalikeJdbcSupport
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional
import scalikejdbc._

/**
 * Created by shimarin on 14/11/12.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations=Array("context.xml"))
@Transactional
class AuthenticationTest extends AnyRef with ScalikeJdbcSupport {

}

package com.wbport.service

import com.wbport.DAO
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

/**
 * Created by shimarin on 14/11/12.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations=Array("context.xml"))
@Transactional
class DAOTest {
  @Autowired private var dao:DAO = _

  @Test def testCreateServer():Unit = {
    val userId = dao.getUser("user@example.com").get.id
    dao.createServer(userId, "hoge.wbport.com")
    assert(dao.createServer(userId, "hoge.wbport.com") == None)
  }
}

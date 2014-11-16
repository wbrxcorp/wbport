package com.wbport

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

/**
 * Created by shimarin on 14/11/16.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations=Array("context.xml"))
@Transactional
class DAOTest extends DAO {
  @Test def testCreateServer():Unit = {
    createServer(1, "hogehoge.wbport.com")
    assert(createServer(1, "hogehoge.wbport.com") == None)
  }
}

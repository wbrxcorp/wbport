package com.wbport.api

import com.wbport.user.RequestHandler
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

/**
 * Created by shimarin on 14/11/14.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations=Array("context.xml"))
@Transactional
class RequestHandlerTest {
  @Autowired private var requestHandler:RequestHandler = _

  @Test def testVpnAuth():Unit = {
    assert(requestHandler.vpnAuth("test.wbport.com", "secret")._1)
    assert(!requestHandler.vpnAuth("test.wbport.com", "wrongpassword")._1)
  }
}

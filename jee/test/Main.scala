import java.awt.Desktop
import java.util.concurrent.TimeUnit

import org.apache.log4j.PropertyConfigurator
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.webapp.WebAppContext
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Created by shimarin on 14/11/12.
 */
object Main {
  def createWebapp(resourceBase:String, contextPath:String):WebAppContext = {
    val webapp = new WebAppContext()

    webapp.setResourceBase(resourceBase)
    webapp.setContextPath(contextPath)
    webapp.setClassLoader(this.getClass().getClassLoader())
    webapp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
    webapp
  }

  def run(port:Option[Int]=None):(Server,Int) = {
    val server = new Server(port.getOrElse(0))
    val handlers = new HandlerList()
    handlers.addHandler(createWebapp("admin/webapp", "/admin"))
    handlers.addHandler(createWebapp("user/webapp", "/user"))
    handlers.addHandler(createWebapp("api/webapp", "/api"))
    handlers.addHandler(createWebapp("ROOT", "/"))
    server.setHandler(handlers)
    server.start()
    (server, server.getConnectors()(0).asInstanceOf[ServerConnector].getLocalPort())
  }

  def main(args: Array[String]): Unit = {
    System.setProperty("spring.profiles.default", "dev")
    System.setProperty("java.util.logging.config.file", "logging.properties")
    PropertyConfigurator.configure("log4j.properties")
    val (server, port) = run()

    System.setProperty("webdriver.chrome.driver", System.getProperty("os.name") match {
      case x if x.startsWith("Windows") => "test/chromedriver.exe"
      case x if x.startsWith("Mac") => "test/chromedriver-mac"
      case x if x.startsWith("Linux") => "test/chromedriver-linux"
      case _ => "test/chromedriver"
    })
    val driver = new ChromeDriver()
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    val url = "http://localhost:%d/".format(port)
    driver.get(url)
  }
}

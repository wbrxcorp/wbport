import java.awt.Desktop

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.webapp.WebAppContext

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
    val (server, port) = run()
    val url = "http://localhost:%d/".format(port)
    println(url)
    if (Desktop.isDesktopSupported) {
      val desktop = Desktop.getDesktop
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        desktop.browse(new java.net.URI(url));
      }
    }
  }
}

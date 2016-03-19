package modules.webapp

import org.eclipse.jetty.server.Server

object Module extends modules.Module {
  private var _server:Server = _

  override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {
    val webAppDefinition = factory.getObjectByName("WebAppDefinition").asInstanceOf[WebAppDefinition]

    webAppDefinition.preInit

    val docRoot = webAppDefinition.docRoot
    if (docRoot.exists(!new java.io.File(_).isDirectory)) return

    val port = webAppDefinition.port
    val root = new org.eclipse.jetty.webapp.WebAppContext()
    root.setBaseResource(new org.eclipse.jetty.util.resource.ResourceCollection(webAppDefinition.docRoot.toArray))
    root.setContextPath("/")
    root.setClassLoader(new java.net.URLClassLoader(new Array[java.net.URL](0), this.getClass().getClassLoader()))
    root.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
    _server = new org.eclipse.jetty.server.Server(port)
    _server.setHandler(root)
    _server.start
    println("http://localhost:%d".format(port))
  }

  override def destroy():Unit = {
    if (_server != null) _server.stop
  }

  def server():Server = this._server

}

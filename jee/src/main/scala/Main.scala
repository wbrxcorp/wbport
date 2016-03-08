object Main extends App {

  private def isRunFromSBT = {
    val c = new java.io.CharArrayWriter()
    new Exception().printStackTrace(new java.io.PrintWriter(c))
    c.toString().contains("at sbt.")
  }

  // main
  val factory = new profiles.Factory(args.toSeq.headOption.getOrElse("default"))

  val modulesToLoad = factory.getObjectByName("ImportModule").asInstanceOf[profiles.ImportModule].modules
  val modulesLoaded = scala.collection.mutable.Set[modules.Module]()

  //http://stackoverflow.com/questions/28052667/scala-repl-crashes-when-started-using-scala-tools-nsc-interpreter
  val repl = new scala.tools.nsc.interpreter.ILoop {
    def loadModule(module:String, modulesLoaded:scala.collection.mutable.Set[modules.Module]):Boolean = {
      val moduleName = "modules.%s.Module".format(module)
      val moduleInitializer = profiles.Factory.getObjectByFullName(moduleName).asInstanceOf[modules.Module]
      if (modulesLoaded(moduleInitializer)) return false
      //else
      modulesLoaded += moduleInitializer

      // load dependencies
      moduleInitializer.dependsOn.foreach(loadModule(_, modulesLoaded))

      echo("Loading module: %s ...".format(module))
      processLine("import %s._".format(moduleName))
      moduleInitializer.init(factory, this)
      true
    }

    override def printWelcome() = {
      super.printWelcome()
      modulesToLoad.foreach(loadModule(_, modulesLoaded))
    }
  }
  val settings = new scala.tools.nsc.Settings
  if (isRunFromSBT) {
    settings.embeddedDefaults[Main.type]
  } else {
    settings.usejavacp.value = true
  }
  repl.process(settings)

  modulesLoaded.foreach(_.destroy)
}

package modules.common

object Module extends modules.Module {
  override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {
    repl.processLine("import scala.collection.JavaConverters._")
  }

  def hello():Unit = println("Hello, World!")
}

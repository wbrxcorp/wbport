package modules

trait Module {
  def dependsOn():Seq[String] = Nil
  def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {}
  def destroy():Unit = {}
}

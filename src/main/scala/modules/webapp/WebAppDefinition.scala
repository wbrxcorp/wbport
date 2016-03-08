package modules.webapp

trait WebAppDefinition {
  def port:Int = 52321
  def docRoot:Seq[String] = Seq("src/main/webapp")
}

package profiles

class Factory(profile:String) {
  def getObjectByName(name:String):Any = {
    scala.util.Try(Factory.getObjectByFullName("profiles.%s.%s".format(profile, name))).getOrElse(Factory.getObjectByFullName("profiles.default.%s".format(name)))
  }
}

object Factory {
  def getObjectByFullName(name:String):Any = {
    val runtimeMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
    val module = runtimeMirror.staticModule(name)
    val obj = runtimeMirror.reflectModule(module)
    obj.instance
  }
}

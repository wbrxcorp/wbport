package modules.common

import java.io.Serializable

object Module extends modules.Module {
  type Closable = { def close():Unit }
  def using[A <: Closable, B](resource:A) (f:A => B) = try(f(resource)) finally(resource.close)


  override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {
    repl.processLine("import scala.collection.JavaConverters._")
  }

  def hello():Unit = println("Hello, World!")

  def saveObject(obj:Serializable, filename:String):Unit = {
    using(new java.io.FileOutputStream(filename)) { fos =>
      using(new java.io.ObjectOutputStream(fos)) { oos =>
        oos.writeObject(obj)
      }
    }
  }

  def loadObject(filename:String):AnyRef = {
    using(new java.io.FileInputStream(filename)) { fis =>
      using(new java.io.ObjectInputStream(fis)) { ois =>
        ois.readObject
      }
    }
  }
}

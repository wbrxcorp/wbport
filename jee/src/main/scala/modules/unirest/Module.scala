package modules.unirest

//import scala.collection.JavaConverters._
import com.mashape.unirest.request.{GetRequest,HttpRequestWithBody}
import com.mashape.unirest.http.JsonNode

object Module extends modules.Module {
  //override def init(factory:profiles.Factory,repl:scala.tools.nsc.interpreter.ILoop):Unit = {}
  //type Closable = { def close():Unit }
  //def using[A <: Closable, B](resource:A) (f:A => B) = try(f(resource)) finally(resource.close)

  override def dependsOn:Seq[String] = Seq("jsonorg")

  object Unirest {
    def get(url:String)(implicit f:GetRequest=>GetRequest = {x=>x}):JsonNode = {
      val request = com.mashape.unirest.http.Unirest.get(url)
      f(request.header("accept",  "application/json")).asJson.getBody
    }

    def post(url:String)(implicit f:HttpRequestWithBody=>HttpRequestWithBody = {x=>x}):JsonNode = {
      val request = com.mashape.unirest.http.Unirest.post(url)
      f(request.header("accept",  "application/json")).asJson.getBody
    }
  }

}

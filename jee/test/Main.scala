import java.util.concurrent.TimeUnit

import org.apache.log4j.PropertyConfigurator
import org.openqa.selenium.chrome.ChromeDriver

import com.walbrix.jetty._

/**
 * Created by shimarin on 14/11/12.
 */
object Main {
  def main(args: Array[String]): Unit = {
    System.setProperty("spring.profiles.default", "dev")
    System.setProperty("java.util.logging.config.file", "logging.properties")
    PropertyConfigurator.configure("log4j.properties")

    val webapps = Seq(
      createWebapp("admin/webapp", "/admin"),
      createWebapp("user/webapp", "/user"),
      createWebapp("api/webapp", "/api"),
      createWebapp("ROOT", "/")
    )

    val (server, port) = run(webapps)

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

package example

import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.Element
import scala.scalajs.js
import org.scalajs.dom
import com.github.marklister.base64.Base64.Encoder

object ScalaJSCode extends js.JSApp {

  def main(): Unit = {
    val kagan = getElementById[Div]("kagan-test")
    if (kagan != null) {
      KaganTest.doTest()
    }
    val gothshild = getElementById[Div]("gothshild-test")
    if (gothshild != null) {
      GothshildTest.doTest()
    }
    val ultraRapid = getElementById[Div]("ultra-rapid")
    if (ultraRapid != null) {
      UltraRapidTest.doTest()
    }
    val stroop = getElementById[Div]("stroop-test")
    if (stroop != null) {
      StroopTest.doTest()
    }
    val kaganEn = getElementById[Div]("kagan-test-en")
    if (kaganEn != null) {
      KaganTestEn.doTest()
    }
    val gothshildEn = getElementById[Div]("gothshild-test-en")
    if (gothshildEn != null) {
      GothshildTestEn.doTest()
    }
    val ultraRapidEn = getElementById[Div]("ultra-rapid-en")
    if (ultraRapidEn != null) {
      UltraRapidTestEn.doTest()
    }
    val stroopEn = getElementById[Div]("stroop-test-en")
    if (stroopEn != null) {
      StroopTestEn.doTest()
    }
  }

  def getElementById[T <: Element](name: String): T = {
    dom.document.getElementById(name).asInstanceOf[T]
  }

  def addNoise(s: String): String = {
    Encoder(s.getBytes("UTF-8")).toBase64()
  }

}

package example

import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.Element
import scala.scalajs.js
import org.scalajs.dom

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
    // todo, need to manage different tests
    // todo a big problem right no
  }

  def getElementById[T <: Element](name: String): T = {
    dom.document.getElementById(name).asInstanceOf[T]
  }

}

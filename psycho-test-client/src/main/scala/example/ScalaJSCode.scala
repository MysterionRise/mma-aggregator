package example

import org.scalajs.dom.raw.Element
import scala.scalajs.js
import org.scalajs.dom

object ScalaJSCode extends js.JSApp {

  def main(): Unit = {
    UltraRapidTest.doTest()
    KaganTest.doTest()
  }

  def getElementById[T <: Element](name: String): T = {
    dom.document.getElementById(name).asInstanceOf[T]
  }

}

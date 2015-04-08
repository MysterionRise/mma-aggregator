package example

import org.scalajs.dom.html.Image
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import org.scalajs.dom
import shared.SharedMessages

object ScalaJSCode extends js.JSApp {
  def main(): Unit = {

    val id: Element = dom.document.getElementById("scalajsShoutOut")
    for (i <- 1 to 8) {
      val e: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
      e.onmousemove = {
        (e1: dom.MouseEvent) =>
          id.textContent =
            s"""e.clientX ${e1.clientX}
                |e.clientY ${e1.clientY}
                |e.pageX   ${e1.pageX}
                |e.pageY   ${e1.pageY}
                |e.screenX ${e1.screenX}
                |e.screenY ${e1.screenY}
         """.stripMargin
      }
    }
  }
}

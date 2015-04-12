package example

import org.scalajs.dom.html.{Div, Image}
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import org.scalajs.dom
import shared.SharedCode._
import shared.{Image => TestImage}

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
      e.onclick = {
        (e1: dom.MouseEvent) =>
          val div: Div = dom.document.getElementById("kagan-test").asInstanceOf[Div]
          val pattern: Image = dom.document.getElementById("pattern").asInstanceOf[Image]
          val img: TestImage = constructImage(pattern.src)
          img.roundNumber += 1 // move to next round
          pattern.src = constructSrc(getPrefix(pattern.src), img)
          // TODO fix
          for (i <- 1 to 8) {
            val prevImg: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
            val img: TestImage = constructImage(prevImg.src)
            img.roundNumber += 1 // move to next round

            prevImg.src = constructSrc(getPrefix(prevImg.src), img)
            // todo check if image exist or not
          }
      }
    }
  }
}

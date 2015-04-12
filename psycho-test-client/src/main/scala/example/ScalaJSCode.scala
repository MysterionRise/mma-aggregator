package example

import org.scalajs.dom.html.{Div, Image}
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import org.scalajs.dom
import shared.SharedMessages
import shared.{Image => TestImage}

object ScalaJSCode extends js.JSApp {

  def constructImage(s: String): TestImage = {
    val paths = s.split("/")
    val len = paths.length
    return new TestImage(paths(len - 3), Integer.parseInt(paths(len - 2)), Integer.parseInt(paths(len - 1).split("\\.")(0)))
  }

  def constructSrc(prefix: String, t: TestImage): String = {
    return prefix + "/" + t.testName + "/" + (t.roundNumber + 1) + "/" + t.imageNumber + ".jpg"
  }

  def getPrefix(s: String): String = {
    val paths = s.split("/")
    val len = paths.length
    var res = ""
    for (i <- 0 until len - 3) {
      res += paths(i) + "/"
    }
    return res
  }

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
          SharedMessages.addToDB(e.id)
          val div: Div = dom.document.getElementById("kagan-test").asInstanceOf[Div]
          val pattern: Image = dom.document.getElementById("pattern").asInstanceOf[Image]
          val img: TestImage = constructImage(pattern.src)
          pattern.src = constructSrc(getPrefix(pattern.src), img)
          // TODO fix
          for (i <- 1 to 8) {
            val prevImg: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
            val img: TestImage = constructImage(prevImg.src)
            prevImg.src = constructSrc(getPrefix(prevImg.src), img)
          }
        // need to show next page
      }
    }
  }
}

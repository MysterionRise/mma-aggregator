package example

import org.scalajs.dom.html.{Div, Image, Heading}
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
          val user: Heading = dom.document.getElementById("user").asInstanceOf[Heading]
          val userID: String = user.getAttribute("data-user-id")
          val div: Div = dom.document.getElementById("kagan-test").asInstanceOf[Div]
          val pattern: Image = dom.document.getElementById("pattern").asInstanceOf[Image]
          val img: TestImage = constructImage(pattern.src)
          saveToDB(userID, img.imageNumber, img.roundNumber, System.currentTimeMillis)
          img.roundNumber += 1 // move to next round
          if (img.roundNumber <= 2) {
            // todo provide correct answer
            pattern.src = constructSrc(getPrefix(pattern.src), img)
            for (i <- 1 to 8) {
              val prevImg: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
              val img: TestImage = constructImage(prevImg.src)
              img.roundNumber += 1 // move to next round
              prevImg.src = constructSrc(getPrefix(prevImg.src), img)
            }
          } else if (img.roundNumber > 14) {
            div.innerHTML = ""
            pattern.setAttribute("hidden", "true")
            div.innerHTML = s"""<form action=/tests/finishTest?userID=${userID} method="POST" class="form-horizontal"><button id="finish-test" type="submit" class="btn btn-primary">Finish Test</button></form>"""
          } else {
            pattern.src = constructSrc(getPrefix(pattern.src), img)
            for (i <- 1 to 8) {
              val prevImg: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
              val img: TestImage = constructImage(prevImg.src)
              img.roundNumber += 1 // move to next round
              prevImg.src = constructSrc(getPrefix(prevImg.src), img)
            }
          }
      }
    }
  }
}

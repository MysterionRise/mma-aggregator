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
      e.onclick = {
        (e1: dom.MouseEvent) =>
          dom.document.cookie = ""
          val user: Heading = dom.document.getElementById("user").asInstanceOf[Heading]
          val userID: String = user.getAttribute("data-user-id")
          val div: Div = dom.document.getElementById("kagan-test").asInstanceOf[Div]
          val pattern: Image = dom.document.getElementById("pattern").asInstanceOf[Image]
          val img: TestImage = constructImage(pattern.src)
          if (img.roundNumber == 1) {
            dom.document.cookie += s"""PLAY_SESSION=${userID}-clicked-on-${img.imageNumber}-in-round-${img.roundNumber}"""
          } else {
            dom.document.cookie += s"""${userID}-clicked-on-${img.imageNumber}-in-round-${img.roundNumber}"""
          }
          img.roundNumber += 1 // move to next round
          img.roundNumber match {
            case 2 | 3 => {
              // todo provide correct answer
              constructNewRound(pattern, img)
            }
            case x if x > 14 => {
              div.innerHTML = ""
              pattern.setAttribute("hidden", "true")
              div.innerHTML = "<form action=/tests/finishTest?report=\"" + dom.document.cookie + "\" method=\"POST\" class=\"form-horizontal\"><button id=\"finish-test\" type=\"submit\" class=\"btn btn-primary\">Finish Test</button></form>"
            }
            case _ => {
              constructNewRound(pattern, img)
            }
          }
      }
    }
  }

  def constructNewRound(pattern: Image, img: TestImage): Unit = {
    pattern.src = constructSrc(getPrefix(pattern.src), img)
    for (i <- 1 to 8) {
      val prevImg: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
      val img: TestImage = constructImage(prevImg.src)
      img.roundNumber += 1 // move to next round
      prevImg.src = constructSrc(getPrefix(prevImg.src), img)
    }
  }
}

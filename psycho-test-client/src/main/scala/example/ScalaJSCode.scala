package example

import org.scalajs.dom.html.{Button, Div, Image, Heading}
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import org.scalajs.dom
import shared.SharedCode._
import shared.{Image => TestImage}

object ScalaJSCode extends js.JSApp {

  def main(): Unit = {

    var testingStarted = false
    val btn = dom.document.getElementById("rapid-button").asInstanceOf[Button]
    btn.onclick = {
      (e: dom.MouseEvent) =>
        testingStarted = true
        val question: Div = dom.document.getElementById("ultra-rapid").asInstanceOf[Div]
        question.innerHTML = "<img src=\"/assets/images/ultraRapid/518.jpg\">"

      //TODO
    }

    dom.document.onkeypress = {
      (e: dom.KeyboardEvent) =>
        if (testingStarted && e.charCode == 32) {
          dom.document.getElementById("ultra-test").textContent = "SPACE PRESSED!"
        }
    }
    val success: Element = dom.document.getElementById("success")
    val error: Element = dom.document.getElementById("error")
    for (i <- 1 to 8) {
      val e: Image = dom.document.getElementById(i.toString).asInstanceOf[Image]
      e.onclick = {
        (e1: dom.MouseEvent) =>
          dom.document.cookie = ""
          success.textContent = ""
          error.textContent = ""
          val user: Heading = dom.document.getElementById("user").asInstanceOf[Heading]
          val userID: String = user.getAttribute("data-user-id")
          val div: Div = dom.document.getElementById("kagan-test").asInstanceOf[Div]
          val pattern: Image = dom.document.getElementById("pattern").asInstanceOf[Image]
          val img: TestImage = constructImage(pattern.src)
          if (img.roundNumber == 1) {
            dom.document.cookie += s"""PLAY_SESSION=${userID}|${e.id}|${img.roundNumber}|${System.currentTimeMillis()}\n"""
          } else {
            dom.document.cookie += s"""|${userID}|${e.id}|${img.roundNumber}|${System.currentTimeMillis()}\n"""
          }
          img.roundNumber += 1 // move to next round
          img.roundNumber match {
            case 2 => {
              provideFeedback(success, error, e, "1")
              constructNewRound(pattern, img)
            }
            case 3 => {
              provideFeedback(success, error, e, "5")
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

  def provideFeedback(success: Element, error: Element, e: Image, correctID: String): Unit = {
    if (e.id.equals(correctID)) {
      success.textContent = "You successfully answer the question!"
    } else {
      error.textContent = " Your answer is INCORRECT!"
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

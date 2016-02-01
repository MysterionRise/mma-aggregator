package example

import example.ScalaJSCode._
import org.scalajs.dom
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element
import shared.SharedCode._
import shared.{Image => TestImage}

object KaganTestEn {

  val report: StringBuilder = new StringBuilder

  def doTest(): Unit = {
    val instruction = getElementById[Div]("instruction")
    val success = getElementById[Element]("success")
    val error = getElementById[Element]("error")
    for (i <- 1 to 8) {
      val e = getElementById[Image](i.toString)
      e.onclick = {
        (e1: dom.MouseEvent) =>
          success.textContent = ""
          error.textContent = ""
          instruction.innerHTML = ""
          val user = getElementById[Heading]("user")
          val userID: String = user.getAttribute("data-user-id")
          val div = getElementById[Div]("kagan-test-en")
          val pattern = getElementById[Image]("pattern")
          val img: TestImage = constructImage(pattern.src)
          report.append(e.id).append("|").append(img.roundNumber).append("|").append(System.currentTimeMillis()).append("|")
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
              submitReport(userID, addNoise(report.toString))
              div.innerHTML = "<h4>Thank you for your time. Testing is now finished. Please, press the button Finish Test</h4><form action=/tests class=\"form-horizontal\"><button id=\"finish-test\" type=\"submit\" class=\"btn btn-primary\">Finish Test</button></form>"
            }
            case _ => constructNewRound(pattern, img)
          }
      }
    }
  }

  private def provideFeedback(success: Element, error: Element, e: Image, correctID: String): Unit = {
    if (e.id.equals(correctID)) {
      success.textContent = "You successfully answered the question!"
    } else {
      error.textContent = " Your answer is INCORRECT!"
    }
  }

  private def constructNewRound(pattern: Image, img: TestImage): Unit = {
    pattern.src = constructSrc(getPrefix(pattern.src), img)
    for (i <- 1 to 8) {
      val prevImg = getElementById[Image](i.toString)
      val img: TestImage = constructImage(prevImg.src)
      img.roundNumber += 1 // move to next round
      prevImg.src = constructSrc(getPrefix(prevImg.src), img)
    }
  }
}

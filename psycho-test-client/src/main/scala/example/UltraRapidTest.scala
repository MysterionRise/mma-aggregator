package example

import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element
import example.ScalaJSCode._

import scala.scalajs.js.Function0

object UltraRapidTest {

  var testingStarted = false

  def clearText(element: Element): Function0[Any] = new Function0[Any] {
    override def apply(): Any = element.textContent = "TAKE A REST"
  }

  def askQuestion(div: Div): Function0[Any] = new Function0[Any] {

    override def apply(): Any = {
      testingStarted = true
      div.innerHTML = ""
      val element = getElementById[Element]("ultra-test")
      element.textContent = "Did you see car here?"
      dom.window.setTimeout(clearText(element), 1000)
    }
  }

  def showImage(div: Div, imageName: String): Function0[Any] = new Function0[Any] {
    override def apply(): Any = {
      div.innerHTML = "<img src=\"/assets/images/ultraRapid/" + imageName + "\">"
      dom.window.setTimeout(askQuestion(div), 33)
    }
  }

  def doTest() = {
    val btn = getElementById[Button]("rapid-button")
    btn.onclick = {
      (e: dom.MouseEvent) =>
        for (i <- 0 until 5) {
          val question = getElementById[Div]("ultra-rapid")

          val canvas = getElementById[Canvas]("ultra-canvas")
          val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

          drawFixationCross(canvas, ctx)
          dom.window.setTimeout(showImage(question, "518.jpg"), 500)
          clearDiv(question)
        }
    }

    dom.document.onkeypress = {
      (e: dom.KeyboardEvent) =>
        if (testingStarted && e.charCode == 32) {
          getElementById[Element]("ultra-test").textContent = "SPACE PRESSED!"

        }
    }
  }

  def drawFixationCross(canvas: Canvas, ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = "gray"
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    ctx.beginPath()
    ctx.strokeStyle = "white"
    val centerX = canvas.width / 2
    val centerY = canvas.height / 2
    ctx.moveTo(centerX, centerY)
    ctx.lineTo(centerX + 50, centerY + 50)
    ctx.stroke()
    ctx.moveTo(centerX, centerY)
    ctx.lineTo(centerX - 50, centerY + 50)

    ctx.stroke()
    ctx.moveTo(centerX, centerY)
    ctx.lineTo(centerX + 50, centerY - 50)

    ctx.stroke()
    ctx.moveTo(centerX, centerY)
    ctx.lineTo(centerX - 50, centerY - 50)

    ctx.stroke()
    ctx.closePath()
  }

  private def clearDiv(div: Div): Function0[Any] = {
    new Function0[Any] {
      override def apply(): Any = div.innerHTML = "<canvas id=\"ultra-canvas\"/>"
    }
  }
}

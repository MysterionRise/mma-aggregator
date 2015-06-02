package example

import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element
import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._

import scala.scalajs.js
import scala.scalajs.js.Function0

object UltraRapidTest {

  var testingStarted = false
  var canvas: Canvas = _
  var ctx: CanvasRenderingContext2D = _

  /**
   * @param imageName - image name, could be empty
   * @param whatToShow - object that will define what to show
   */
  case class State(imageName: String, whatToShow: WhatToShow, numberOfQuestions: Int)


  class Backend($: BackendScope[_, State]) {
    var interval: js.UndefOr[js.timers.SetIntervalHandle] =
      js.undefined

    def clearAndSetInterval(interval: js.UndefOr[js.timers.SetIntervalHandle], duration: Int) = {
      js.timers.clearInterval(interval.get)
      this.interval = js.timers.setInterval(duration)(showPicture())
    }

    def showPicture(): Unit =
      $.modState(s => {
        s.whatToShow match {
          case r: Rest => {
            val next = r.moveToNext()
            clearAndSetInterval(interval, next.getDuration)
            val sp = s.imageName.split("\\.")
            val num = Integer.parseInt(sp(0))
            State((num + 1).toString + "." + sp(1), next, s.numberOfQuestions - 1)
          }
          case w: WhatToShow => {
            val nextState = w.moveToNext
            clearAndSetInterval(interval, nextState.getDuration)
            State(s.imageName, nextState, s.numberOfQuestions)
          }
        }
      })

    def init(state: State) =
    // todo create new report
      interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture())
  }

  val testApp = ReactComponentB[Unit]("TestApp")
    .initialState(State("551.jpg", FixationCross(750), 5))
    .backend(new Backend(_))
    .render((_, S, _) => {
    if (S.numberOfQuestions > 0) {
      S.whatToShow match {
        case FixationCross(_) => img(src := "/assets/images/cross.png")
        case ImageQuestion(_) => img(src := "/assets/images/ultraRapid/" + S.imageName)
        case TextQuestion(_) => {
          dom.document.onkeypress = {
            (e: dom.KeyboardEvent) =>
              if (e.charCode == 32) {
                getElementById[Element]("ultra-test").textContent = "SPACE PRESSED!"
                // TODO add report
              }
          }
          p("Did you see animal here?")
        }
        case Rest(_) => {
          getElementById[Element]("ultra-test").textContent = ""
          h2("Take a rest, please!")
        }
        case _ => h1("Something goes wrong!")
      }
    } else {
      div(form(
        formAction := "/tests/finishTest?report=\"" + dom.document.cookie + "\" method=\"POST\"",
        `class` := "form-horizontal",
        button(
          id := "finish-test",
          `type` := "submit",
          `class` := "btn btn-primary",
          "Finish Test"
        )
      ))
    }
  })
    .componentDidMount(f => {
    f.backend.init(f.state)
  })
    .buildU

  def doTest() = {
    val question = getElementById[Div]("ultra-rapid")
    val btn = getElementById[Button]("rapid-button")
    btn.onclick = {
      (e: dom.MouseEvent) => {
        React.render(testApp(), question)
        btn.setAttribute("disabled", "true")
      }
    }
  }

  def drawFixationCross(): Function0[Any] = new Function0[Any] {
    override def apply(): Any = {
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
  }
}

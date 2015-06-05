package example

import org.scalajs.dom
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element
import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._

import scala.scalajs.js

object UltraRapidTest {

  var testingStarted = false

  /**
   * @param imageName - image name, could be empty
   * @param whatToShow - object that will define what to show
   */
  case class State(imageName: String, whatToShow: WhatToShow, numberOfQuestions: Int)


  class Backend(stateController: BackendScope[_, State]) {
    var interval: js.UndefOr[js.timers.SetIntervalHandle] =
      js.undefined

    def clearAndSetInterval(interval: js.UndefOr[js.timers.SetIntervalHandle], duration: Int) = {
      js.timers.clearInterval(interval.get)
      this.interval = js.timers.setInterval(duration)(showPicture())
    }

    def showPicture(): Unit =
      stateController.modState(s => {
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

    def init(state: State) = {
      dom.document.cookie = ""
      interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture())
    }
  }

  val testApp = ReactComponentB[Unit]("TestApp")
    .initialState(State("551.jpg", FixationCross(500), 5))
    .backend(new Backend(_))
    .render((_, S, B) => {
    if (S.numberOfQuestions > 0) {
      S.whatToShow match {
        case FixationCross(_) => img(src := "/assets/images/cross.png")
        case CorrectAnswerCross(_) => img(src := "/assets/images/cross.png")
        case IncorrectAnswerCross(_) => img(src := "/assets/images/cross.png")
        case ImageQuestion(_) => img(src := "/assets/images/ultraRapid/" + S.imageName)
        case TextQuestion(_) => {
          dom.document.onkeypress = {
            (e: dom.KeyboardEvent) =>
              if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
                getElementById[Element]("ultra-test").textContent = "SPACE PRESSED!"
                val user = getElementById[Heading]("user")
                var userID: String = user.getAttribute("data-user-id")
                if (userID.isEmpty) {
                  userID = "123"
                }
                if (!testingStarted) {
                  dom.document.cookie += s"PLAY_SESSION=$userID|${S.imageName}\n"
                  testingStarted = true
                } else {
                  dom.document.cookie += s"|$userID|${S.imageName}\n"
                }
                B.showPicture()
              }
          }
          p("Did you see animal here?")
        }
        case Rest(_) => {
          dom.document.onkeypress = {
            (e: dom.KeyboardEvent) => {}
          }
          getElementById[Element]("ultra-test").textContent = ""
          h2("Take a rest, please!")
        }
        case _ => h1("Something goes wrong!")
      }
    } else {
      js.timers.clearInterval(B.interval.get)
      if (dom.document.cookie.isEmpty) {
        dom.document.cookie += s"PLAY_SESSION"
      }
      div(form(
        action := "/tests/finishTest?report=\"" + dom.document.cookie + "\"",
        `class` := "form-horizontal",
        method := "POST",
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
        dom.document.cookie = ""
        React.render(testApp(), question)
        btn.setAttribute("disabled", "true")
      }
    }
  }
}

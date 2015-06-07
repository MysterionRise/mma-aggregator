package example

import org.scalajs.dom
import org.scalajs.dom.html._
import org.scalajs.dom.raw.Element
import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.util.Random

object UltraRapidTest {

  var testingStarted = false
  var notClicked = true

  /**
   * @param imageName - image name, could be empty
   * @param whatToShow - object that will define what to show
   */
  case class State(imageName: String, whatToShow: WhatToShow, isTesting: Boolean, images: ArrayBuffer[String])

  class Report(userName: String) {
    val answers = new ArrayBuffer[(Int, Int, Int)]()

    def addAnswerToReport(imageId: Int, answerId: Int, questionId: Int) = {
      answers += ((imageId, answerId, questionId))
    }

    def createReport(answers: ArrayBuffer[(Int, Int, Int)]): String = {
      answers.size match {
        case 0 => ""
        case n: Int => {
          val x = answers.head
          s"$x|" + createReport(answers.tail)
        }
      }
    }

    override def toString: String = {
      s"$userName|${createReport(answers)}"
    }
  }

  class Backend(stateController: BackendScope[_, State]) {
    val user = getElementById[Heading]("user")
    var userID: String = user.getAttribute("data-user-id")
    if (userID.isEmpty) {
      // todo for testing purposes
      userID = "123"
    }
    val report = new Report(userID)
    var interval: js.UndefOr[js.timers.SetIntervalHandle] =
      js.undefined

    def clearAndSetInterval(interval: js.UndefOr[js.timers.SetIntervalHandle], duration: Int) = {
      js.timers.clearInterval(interval.get)
      this.interval = js.timers.setInterval(duration)(showPicture())
    }

    def fromBooleanToInt(b: Boolean): Int = if (b) 1 else 0

    def getCorectAnswerByName(s: String): Boolean = {
      s.startsWith("y_")
    }

    def showPicture(): Unit =
      stateController.modState(s => {
        s.whatToShow match {
          case r: Rest => {
            val next = r.moveToNext(fromBooleanToInt(s.isTesting))
            clearAndSetInterval(interval, next.getDuration)
            val idx = new Random().nextInt(s.images.size)
            State(s.images.remove(idx), next, s.isTesting, s.images.result())
          }
          case t: TextQuestion => {
            if (s.isTesting) {
              var nextState: WhatToShow = null
              val correctAnswer = getCorectAnswerByName(s.imageName)
              if (notClicked && !correctAnswer) {
                report.addAnswerToReport(s.imageName.hashCode, 1, 1)
                nextState = t.moveToNext(1)
              } else if (!notClicked && correctAnswer) {
                report.addAnswerToReport(s.imageName.hashCode, 2, 1)
                nextState = t.moveToNext(1)
              } else {
                report.addAnswerToReport(s.imageName.hashCode, 3, 1)
                nextState = t.moveToNext(0)
              }
              notClicked = true
              clearAndSetInterval(interval, nextState.getDuration)
              State(s.imageName, nextState, s.isTesting, s.images)
            } else {
              val nextState = t.moveToNext(2)
              clearAndSetInterval(interval, nextState.getDuration)
              State(s.imageName, nextState, s.isTesting, s.images)
            }
          }
          case w: WhatToShow => {
            val nextState = w.moveToNext(fromBooleanToInt(s.isTesting))
            clearAndSetInterval(interval, nextState.getDuration)
            State(s.imageName, nextState, s.isTesting, s.images)
          }
        }
      })

    def init(state: State) = {
      dom.document.cookie = ""
      interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture())
    }
  }

  // todo generate test stuff
  private val strings = ArrayBuffer("n_1", "n_2", "n_3", "n_51", "n_52",
    "n_53", "y_316", "y_317", "y_318", "y_319", "y_320")
  val testApp = ReactComponentB[Unit]("TestApp")
    .initialState(State(strings.remove(new Random().nextInt(strings.size)), FixationCross(500), true, strings))
    .backend(new Backend(_))
    .render((_, S, B) => {
    if (!S.images.isEmpty) {
      S.whatToShow match {
        case FixationCross(_) => img(src := "/assets/images/cross.png")
        case CorrectAnswerCross(_) => img(src := "/assets/images/cross-correct.png")
        case IncorrectAnswerCross(_) => img(src := "/assets/images/cross-incorrect.png")
        case ImageQuestion(_) => img(src := "/assets/images/ultraRapid/" + S.imageName + ".jpg")
        case TextQuestion(_) => {
          dom.document.onkeypress = {
            (e: dom.KeyboardEvent) =>
              if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
                getElementById[Element]("ultra-test").textContent = "SPACE PRESSED!"
                val user = getElementById[Heading]("user")
                var userID: String = user.getAttribute("data-user-id")
                // TODO for testing purposes only
                if (userID.isEmpty) {
                  userID = "123"
                }
                notClicked = false
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
        action := "/tests/finishTest?report=\"" + B.report.toString + "\"",
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

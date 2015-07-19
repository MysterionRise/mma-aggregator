package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html._

object StroopTest {

  private val question = getElementById[Div]("stroop-test")
  private val instruction = getElementById[Div]("instruction")
  // TODO change size of trials
  private val sizeOfTrials = 10

  def doTest() = {
    React.render(buttonApp.apply(), question)
  }

  val buttonApp = ReactComponentB[Unit]("StartButton")
    .initialState(State(1, 1))
    .backend(new TestBackend(_))
    .render((_, S, B) => button(
    `class` := "btn btn-primary",
    onClick ==> B.startTest,
    "Start test!"
  )
    )
    .buildU

  /**
   *
   * @param taskNumber
   * @param trialNumber
   * 1 - black font, names of colors
   * 2 - colored rectangles
   * 3 - names of colors with wrong font color
   */
  case class State(taskNumber: Int, trialNumber: Int)

  class TestBackend($: BackendScope[_, State]) {
    private var time = System.currentTimeMillis()
    private val report: StringBuilder = new StringBuilder

    def showNextQuestion() = {
      $.modState(s => {
        if (s.taskNumber == sizeOfTrials) {
          State(1, s.trialNumber + 1)
        } else {
          State(s.taskNumber + 1, s.trialNumber)
        }
      })
      time = System.currentTimeMillis()
    }

    def handleInput(e: ReactEventI)(answer: Int) = {
      report.append(s"${answer}|${System.currentTimeMillis() - time}|")
    }

    var currentAnswer = 1

    def startTest(e: ReactEventI) = {
      val gTest = ReactComponentB[Unit]("StartButton")
        .initialState(State(1, 1))
        .backend(new TestBackend(_))
        .render((_, S, B) => {
        dom.window.addEventListener("keypress", { e: KeyboardEvent => {
          showNextQuestion()
          e.key match {
            case "ж" => {

            }
            case "к" => {

            }
            case "з" => {

            }
            case "с" => {

            }
          }
        }
        })
        S.trialNumber match {
          case 1 => {
            br
          }
          case 2 => {
            br
          }
          case 3 => {
            br
          }
          case _ => {
            val user = getElementById[Heading]("user")
            val userID = user.getAttribute("data-user-id")
            div(
              h4("Спасибо за выполненную работу. Тестирование закончено. Нажмите, пожалуйста, кнопку Finish Test"),
              form(
                action := "/tests/finishTest?report=\"" + userID + "=" + addNoise(B.report.toString) + "\"",
                `class` := "form-horizontal",
                method := "POST",
                button(
                  id := "finish-test",
                  `type` := "submit",
                  `class` := "btn btn-primary",
                  "Finish test"
                )
              )
            )
          }
        }
      })
        .buildU
      React.render(gTest.apply(), question)
      instruction.innerHTML = ""
      time = System.currentTimeMillis()
    }
  }

}

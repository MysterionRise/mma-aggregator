package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html._

import scala.util.Random

object StroopTestEn {

  private val question = getElementById[Div]("stroop-test-en")
  private val instruction = getElementById[Div]("instruction")
  // TODO change size of trials
  private val sizeOfTrials = 50
  private var correctAnswer = -1
  private var userAnswer = -1

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
      if (correctAnswer == userAnswer) {
        addReport(2)
      } else {
        addReport(1)
      }
      time = System.currentTimeMillis()
      $.modState(s => {
        if (s.taskNumber == sizeOfTrials) {
          State(1, s.trialNumber + 1)
        } else {
          State(s.taskNumber + 1, s.trialNumber)
        }
      })
    }

    def addReport(answer: Int) = {
      report.append(s"${answer}|${System.currentTimeMillis() - time}|")
    }

    def startTest(e: ReactEventI) = {
      val gTest = ReactComponentB[Unit]("StartButton")
        .initialState(State(1, 1))
        .backend(new TestBackend(_))
        .render((_, S, B) => {
        dom.window.onkeyup = { (e: dom.Event) =>
          val event = e.asInstanceOf[KeyboardEvent]
          event.keyCode match {
            case 66 => {
              userAnswer = 0
              B.showNextQuestion()
            }
            case 71 => {
              userAnswer = 1
              B.showNextQuestion()
            }
            case 82 => {
              userAnswer = 2
              B.showNextQuestion()
            }
            case 89 => {
              userAnswer = 3
              B.showNextQuestion()
            }
            case _ => {
            }
          }
        }
        S.trialNumber match {
          case 1 => {
            instruction.innerHTML = "During the first series of color will be written in black on a gray background. You will need to read the word and press the keyboard letter that begins the word. For example, brought the word \"yellow\"; the correct answer - pressing \"y\"\n"
            generateTrial1()
          }
          case 2 => {
            instruction.innerHTML = "During the second series will be presented to you a rectangular painted in different colors. Your task is to identify the paint and press the letter on the keyboard, which begins this paint color. For example, the rectangle is painted with red color, then correct answer is to press \"r\""
            generateTrial2()
          }
          case 3 => {
            instruction.innerHTML = "During the third series you will see the words printed with inappropriate color values. You have to press the letter that begins the color of the paint, which is printed word. For example, the word \"yellow\" is printed in blue paint. In this case, the correct answer - key \"b\".\n"
            generateTrial3()
          }
          case _ => {
            dom.window.onkeyup = { (e: dom.Event) => }
            val user = getElementById[Heading]("user")
            val userID = user.getAttribute("data-user-id")
            div(
              h4("Thank you for your time. Testing is now finished. Please, press the button Finish Test"),
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

      time = System.currentTimeMillis()
      React.render(gTest.apply(), question)
    }
  }

  val colorTexts = Array("blue", "green", "red", "yellow")
  val colors = Array("blue", "green", "red", "yellow")
  val random = new Random()

  def generateTrial1() = {
    val idx = random.nextInt(colorTexts.length)
    correctAnswer = idx
    div(
      height := "30%",
      width := "30%",
      marginLeft := "auto",
      marginRight := "auto",
      h2(
        textAlign := "center",
        colorTexts(idx))
    )
  }

  def generateTrial2() = {
    val idx = random.nextInt(colorTexts.length)
    correctAnswer = idx
    div(
      height := "30%",
      width := "30%",
      marginLeft := "auto",
      marginRight := "auto",
      div(width := "100%", height := "80%", backgroundColor := colors(idx))
    )
  }

  def generateTrial3() = {
    val idx = random.nextInt(colorTexts.length)
    var colorIdx = idx
    while (colorIdx == idx) {
      colorIdx = random.nextInt(colors.length)
    }
    correctAnswer = colorIdx
    div(
      height := "30%",
      width := "30%",
      marginLeft := "auto",
      marginRight := "auto",
      h2(
        textAlign := "center",
        color := colors(colorIdx),
        colorTexts(idx)
      )
    )
  }

}

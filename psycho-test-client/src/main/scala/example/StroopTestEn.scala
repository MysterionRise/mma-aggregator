package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html._

import scala.util.Random

object StroopTestEn {

  private val question = getElementById[Div]("stroop-test")
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
            case 67 => {
              userAnswer = 0
              B.showNextQuestion()
            }
            case 80 => {
              userAnswer = 1
              B.showNextQuestion()
            }
            case 82 => {
              userAnswer = 2
              B.showNextQuestion()
            }
            case 186 => {
              userAnswer = 3
              B.showNextQuestion()
            }
            case _ => {
            }
          }
        }
        S.trialNumber match {
          case 1 => {
            instruction.innerHTML = "Цвета будут написаны черной краской на сером фоне. Вам необходимо будет прочитать слово и нажать на клавиатуре букву, с которой начинается это слово.Например, предъявлено слово \"желтый\"; правильный ответ – нажатие клавиши \"ж\".\n        "
            generateTrial1()
          }
          case 2 => {
            instruction.innerHTML = "Вам будут предъявлены прямоугольные карты залитые разной краской. Ваша задача опознать краску и нажать на клавиатуре букву, с которой начинается цвет этой краски. Например, предъявлен прямоугольник красного цвета; правильный ответ - нажатие клавиши \"к\".\n        "
            generateTrial2()
          }
          case 3 => {
            instruction.innerHTML = "Вы увидите слова напечатанные краской несоответствующей значениям цветов. Вам необходимо нажать букву, с которой начинается цвет краски, которой напечатано слово. Например, слово «желтый» будет напечатано синей краской. В данном случае правильный ответ – клавиша «с».\n        "
            generateTrial3()
          }
          case _ => {
            dom.window.onkeyup = { (e: dom.Event) => }
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

      time = System.currentTimeMillis()
      React.render(gTest.apply(), question)
    }
  }

  val colorTexts = Array("синий", "зеленый", "красный", "желтый")
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

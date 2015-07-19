package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.html._

object GothshildTest {

  private val question = getElementById[Div]("gothshild-test")
  private val instruction = getElementById[Div]("instruction")

  def doTest() = {
    React.render(buttonApp.apply(), question)
  }

  val buttonApp = ReactComponentB[Unit]("StartButton")
    .initialState(State(1))
    .backend(new TestBackend(_))
    .render((_, S, B) => button(
    `class` := "btn btn-primary",
    onClick ==> B.startTest,
    "Start test!"
  )
    )
    .buildU


  case class State(taskNumber: Int)

  class TestBackend($: BackendScope[_, State]) {
    private var time = System.currentTimeMillis()
    private val report: StringBuilder = new StringBuilder

    def showNextQuestion() = {
      $.modState(s => State(s.taskNumber + 1))
      time = System.currentTimeMillis()
    }

    def clickA(e: ReactEventI) = {
      report.append(s"${$.state.taskNumber}|A|${System.currentTimeMillis() - time}|")
      showNextQuestion()
    }

    def clickB(e: ReactEventI) = {
      report.append(s"${$.state.taskNumber}|B|${System.currentTimeMillis() - time}|")
      showNextQuestion()
    }

    def clickV(e: ReactEventI) = {
      report.append(s"${$.state.taskNumber}|V|${System.currentTimeMillis() - time}|")
      showNextQuestion()
    }

    def clickG(e: ReactEventI) = {
      report.append(s"${$.state.taskNumber}|G|${System.currentTimeMillis() - time}|")
      showNextQuestion()
    }

    def clickD(e: ReactEventI) = {
      report.append(s"${$.state.taskNumber}|D|${System.currentTimeMillis() - time}|")
      showNextQuestion()
    }

    def skipQuestion(e: ReactEventI) = {
      report.append(s"${$.state.taskNumber}|SKIP|${System.currentTimeMillis() - time}|")
      showNextQuestion()
    }


    def startTest(e: ReactEventI) = {
      val gTest = ReactComponentB[Unit]("StartButton")
        .initialState(State(1))
        .backend(new TestBackend(_))
        .render((_, S, B) =>
        if (S.taskNumber == 31) {
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
        } else {
          div(
            h4("Какой из элементов содержится в сложном рисунке?"),
            div(
              `class` := "jumbotron",
              width := "700px",
              marginLeft := "auto",
              marginRight := "auto",
              a(img(src := "/assets/images/gothshild/A.jpg"), onClick ==> B.clickA),
              a(img(src := "/assets/images/gothshild/B.jpg"), onClick ==> B.clickB),
              a(img(src := "/assets/images/gothshild/V.jpg"), onClick ==> B.clickV),
              a(img(src := "/assets/images/gothshild/G.jpg"), onClick ==> B.clickG),
              a(img(src := "/assets/images/gothshild/D.jpg"), onClick ==> B.clickD),
              br,
              br,
              br,
              button(
                `class` := "btn btn-primary",
                onClick ==> B.skipQuestion,
                "Пропустить задание"
              )
            ),
            br,
            img(src := s"/assets/images/gothshild/tasks/${S.taskNumber}.jpg", marginLeft := "auto", marginRight := "auto", display := "block")
          )
        }
        )
        .buildU
      React.render(gTest.apply(), question)
      instruction.innerHTML = ""
      time = System.currentTimeMillis()
    }
  }

}

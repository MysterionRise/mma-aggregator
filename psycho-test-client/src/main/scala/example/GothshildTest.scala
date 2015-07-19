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
    .initialState("")
    .backend(new TestBackend(_))
    .render((_, S, B) => button(
    `class` := "btn btn-primary",
    onClick ==> B.startTest,
    "Start test!"
  )
    )
    .buildU

  class TestBackend($: BackendScope[_, String]) {
    var taskNumber = 1

    def clickA(e: ReactEventI) = {

    }

    def clickB(e: ReactEventI) = {
    }

    def clickV(e: ReactEventI) = {
    }

    def clickG(e: ReactEventI) = {
    }

    def clickD(e: ReactEventI) = {
    }


    def startTest(e: ReactEventI) = {
      val gTest = ReactComponentB[Unit]("StartButton")
        .initialState("")
        .backend(new TestBackend(_))
        .render((_, S, B) =>
        div(
          div(
            `class` := "jumbotron",
            width := "700px",
            marginLeft := "auto",
            marginRight := "auto",
            img(src := "/assets/images/gothshild/A.jpg", onClick ==> B.clickA),
            img(src := "/assets/images/gothshild/B.jpg", onClick ==> B.clickB),
            img(src := "/assets/images/gothshild/V.jpg", onClick ==> B.clickV),
            img(src := "/assets/images/gothshild/G.jpg", onClick ==> B.clickG),
            img(src := "/assets/images/gothshild/D.jpg", onClick ==> B.clickD)
          ),
          br,
          br,
          img(src := s"/assets/images/gothshild/tasks/${taskNumber}.jpg", marginLeft := "auto", marginRight := "auto", display := "block")
        )
        )
        .buildU
      React.render(gTest.apply(), question)
      instruction.innerHTML = ""
    }
  }

}

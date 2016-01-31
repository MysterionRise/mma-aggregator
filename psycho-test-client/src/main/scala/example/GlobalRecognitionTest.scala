package example

import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.html._
import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import shared.SharedCode._
import shared.UltraRapidImage
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle

object GlobalRecognitionTest {

  private val testQuestionAmount = 29
  private var backend: scala.Option[Backend2] = None
  private val question = getElementById[Div]("global-recognition")
  private val interval: js.UndefOr[js.timers.SetIntervalHandle] = js.undefined

  private def getBackend(sc: BackendScope[_, State]): Backend2 = {
    backend match {
      case None => backend = Some(new Backend2(sc, true, None))
      case Some(x) => {
        val b = new Backend2(sc, true, Some(x.report.get))
        backend = Some(b)
      }
    }
    backend.get
  }

  def constructArrayBuffer(s: String) = {
    val bigDiv = dom.document.getElementById("big").asInstanceOf[Div]
    val res = new ArrayBuffer[UltraRapidImage]()
    val pairs = s.split(";")
    val div = dom.document.createElement("div").asInstanceOf[Div]
    div.setAttribute("hidden", "true")
    div.id = "preload-div"
    getElementById[Body]("body").appendChild(div)
    var loaded = 0
    var size = 0
    for (pair <- pairs) {
      size += 1
      val image = UltraRapidImage(pair.split(",")(0), pair.split(",")(1), false)
      res.append(image)
      val newChild = dom.document.createElement("img").asInstanceOf[Image]
      newChild.src = "/assets/images/test2/open_experiment/" + image.imageName + ".jpg"
      // TODO wait till 0.8.2 release
      newChild.addEventListener("load", { e: Event => {
        loaded += 1
        image.preloaded = true
      }
      })
      getElementById[Div]("preload-div").appendChild(newChild)
    }
    var preloadInterval: SetIntervalHandle = null
    preloadInterval = js.timers.setInterval(500)({
      getElementById[Div]("loading-bar").style.width = s"${(100 * loaded) / size}%"
      if (loaded == size) {
        bigDiv.setAttribute("hidden", "false")
        js.timers.clearInterval(preloadInterval)
      }
    })
    val cross = dom.document.createElement("img").asInstanceOf[Image]
    cross.src = "/assets/images/cross.png"
    getElementById[Div]("preload-div").appendChild(cross)
    util.Random.shuffle(res)
  }

  private val strings = constructArrayBuffer(getElementById[Div]("images").getAttribute("data-images"))

  def getRandomQuestion(images: ArrayBuffer[UltraRapidImage]): (UltraRapidImage, ArrayBuffer[UltraRapidImage]) = {
    if (!images.isEmpty) {
      val idx = generateRandomIndex(images.length)
      val img = images.remove(idx)
      (img, images)
    } else
      (null, null)
  }

  val buttonApp = ReactComponentB[Unit]("StartButton")
    .initialState("")
    .backend(new TestBackend(_))
    .render((_, S, B) =>
    button(`class` := "btn btn-primary", onClick ==> B.startTest, "Start test!")
    )
    .buildU

  class TestBackend($: BackendScope[_, String]) {

    def startTest(e: ReactEventI) = {

      val questionTypes = new ArrayBuffer[Int]()
      questionTypes.append(0)
      val testApp = ReactComponentB[Unit]("TestSession")
        .initialState(StateObj.apply(getRandomQuestion(strings), FixationCross(500, false), false,
        1, testQuestionAmount, true))
        .backend(sc => getBackend(sc))
        .render((_, S, B) => {
        val user = getElementById[Heading]("user")
        val userID: String = user.getAttribute("data-user-id")
        if (S.numberOfQuestions > 0) {
          S.whatToShow match {
            case FixationCross(_, _) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case ImageQuestion(_, _) => img(src := "/assets/images/test2/open_experiment/" + S.res._1.imageName + ".jpg", marginLeft := "auto", marginRight := "auto", display := "block")
            case TextQuestion(_, _) => {
              div()
            }
            case NoNextState(_) => {
              div(
                `class` := "bs-component",
                form(
                  `class` := "form-horizontal",
                  onSubmit ==> B.nextImage,
                  textarea(id := "response", placeholder := "Опишите увиденное изображение!",
                    onChange ==> B.addText,
                    autoFocus := true,
                    rows := 10, cols := 70, `class` := "form-control"),
                  button("Продолжить!", `class` := "btn btn-primary")
                )
              )
            }
            case Rest(_, _) => {
              // reduce number of questions to be asked for this type of a question
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) => {}
              }
              div()
            }
          }
        } else {
          js.timers.clearInterval(B.interval.get)
          div(
            h4("Спасибо за выполненную работу. Тестирование закончено. Нажмите, пожалуйста, кнопку Finish Test"),
            form(
              action := "/tests/finishTest?report=\"" + userID + "=" + addNoise(B.report.get.answers.toString) + "\"",
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
      })
        .componentDidMount(f => {
        f.backend.init(f.state, questionTypes, testQuestionAmount)
      })
        .buildU
      React.render(testApp(), question)
      getElementById[Div]("instruction").innerHTML = ""
      $.setState("")
    }
  }

  def doTest() = {
    React.render(buttonApp.apply(), question)
  }

  def clearInterval = {
    js.timers.clearInterval(interval.get)
  }


}

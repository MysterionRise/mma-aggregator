package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom.html._
import shared.SharedCode._
import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle

object MultiChoiceTest {

  private val questionAmount = 70
  private val questionTypes = util.Random.shuffle(ArrayBuffer(1, 2, 3, 4, 5, 6))
  private var backend: scala.Option[MultiChoiceBackend] = None
  private val question = getElementById[Div]("multi-choice-test")
  private val interval: js.UndefOr[js.timers.SetIntervalHandle] = js.undefined

  private def getBackend(sc: BackendScope[_, MultiChoiceState]): MultiChoiceBackend = {
    backend match {
      case None => backend = Some(new MultiChoiceBackend(sc, true, None))
      case Some(x) => {
        val b = new MultiChoiceBackend(sc, true, Some(x.report.get))
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

  private val testStrings = constructArrayBuffer(getElementById[Div]("images").getAttribute("data-images"))

  def customP(innerText: String): ReactElement = {
    div(
      p(
        innerText,
        position := "absolute",
        top := "50%",
        left := "50%",
        marginRight := "-50%",
        transform := "translate(-50%, -50%)"
      )
    )
  }

  /**
   * Mapping for targets non-targets in questions
   */
  val mapping = new Array[Set[Int]](10)
  mapping(1) = Set(1, 2, 3)
  mapping(2) = Set(2, 3, 4)
  mapping(3) = Set(1, 3, 4)
  mapping(4) = Set(1, 2, 4)
  mapping(5) = Set(5, 6)
  mapping(6) = Set(5, 6)
  mapping(7) = Set(7)
  mapping(8) = Set(7)

  def getRandomQuestion(images: ArrayBuffer[UltraRapidImage], qType: Int): (UltraRapidImage, ArrayBuffer[UltraRapidImage]) = {
    val s = mapping.apply(qType)
    var idx = generateRandomIndex(images.length)
    var cnt = 0
    // todo fix if we don't have targets or non-targets for this type of a question
    while (images(idx).preloaded && !s.contains(Integer.parseInt(images(idx).imageType)) && cnt < images.length) {
      idx = generateRandomIndex(images.length)
      cnt += 1
    }
    val img = images.remove(idx)
    (img, images)
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

    def askQuestion(questionType: String): String = {
      questionType match {
        case "1" => "На этом изображении есть собака?"
        case "2" => "На этом изображении есть животное?"
        case "3" => "На этом изображении есть легковой автомобиль?"
        case "4" => "На этом изображении есть транспортное средство?"
        case "5" => "Это изображение природы?"
        case "6" => "Это изображение объектов, созданных человеком?"
        case "7" => "Событие происходит в помещении?"
        case "8" => "Изображено позитивное взаимодействие людей?"
        case _ => "We don't have any questions for that type!"
      }
    }

    def startTest(e: ReactEventI) = {

      val realTestQType = questionTypes.remove(0)
      val realTestApp = ReactComponentB[Unit]("RealSession")
        .initialState(MultiChoiceState(getRandomQuestion(testStrings, realTestQType), Cross(500),
        realTestQType, 0))
        .backend(getBackend(_))
        .render((_, S, B) => {
        if (S.questionType > 0) {
          S.whatToShow match {
            case Mask(_) => img(src := "/assets/images/mask.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case Cross(_) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case ImageQ(_) => img(src := "/assets/images/test2/open_experiment/" + S.res._1.imageName + ".jpg", marginLeft := "auto", marginRight := "auto", display := "block")
            case ChoiceQuestion(_) => {
              // todo need to show proper answers based on question
              div(
                `class` := "bs-component",
                form(
                  `class` := "form-horizontal",
                  onSubmit ==> B.nextImage1,
                  button(askQuestion(S.res._1.imageType), `class` := "btn btn-primary")
                ),
                p(),
                form(
                  `class` := "form-horizontal",
                  onSubmit ==> B.nextImage2,
                  button(askQuestion(S.res._1.imageType), `class` := "btn btn-primary")
                ),
                p(),
                form(
                  `class` := "form-horizontal",
                  onSubmit ==> B.nextImage3,
                  button(askQuestion(S.res._1.imageType), `class` := "btn btn-primary")
                ))

            }
            case RestPeriod(_) => {
              // reduce number of questions to be asked for this type of a question
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) => {}
              }
              div()
            }
          }
        } else {
          // todo save report
          div(
            h4("Спасибо за выполненную работу. Тестирование закончено. Нажмите, пожалуйста, кнопку Finish Test"),
            form(
              action := "/tests",
              `class` := "form-horizontal",
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
        f.backend.init(f.state, questionTypes, questionAmount)
      })
        .buildU

      React.render(realTestApp(), question)
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

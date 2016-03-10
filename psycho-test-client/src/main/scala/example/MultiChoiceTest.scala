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
import scala.util.Random

object MultiChoiceTest {

  private val questionAmount = 180
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
      newChild.src = "/assets/images/multiChoice/" + image.imageType.split("_").mkString("/") + "/" + image.imageName + ".jpg"
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
    // todo, do we really need it
    //    while (images(idx).preloaded && !s.contains(Integer.parseInt(images(idx).imageType)) && cnt < images.length) {
    //      idx = generateRandomIndex(images.length)
    //      cnt += 1
    //    }
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

    val questions = List[String](
      "Cобака                     ",
      "Животное                   ",
      "Легковой автомобиль        ",
      "Транспортное средство      ",
      "Природные объекты          ",
      "Городские постройки        "
    )
    val questions1 = List[String](
      "Поезд                      ",
      "Поезд                      ",
      "Медведь                    ",
      "Медведь                    ",
      "Природные объекты          ",
      "Городские постройки        "
    )
    val questions2 = List[String](
      "Легковой автомобиль        ",
      "Легковой автомобиль        ",
      "Собака                     ",
      "Собака                     ",
      "Посмотреть картинку еще раз",
      "Посмотреть картинку еще раз"
    )

    def askQuestion(questionType: String, id: Int, correctAnswer: Int): String = {
      if (id == correctAnswer) {
        questionType.charAt(0) match {
          case '1' => questions(0)
          case '2' => questions(1)
          case '3' => questions(2)
          case '4' => questions(3)
          case '5' => questions(4)
          case '6' => questions(5)
          case _ => "-------------------"
        }
      } else if (correctAnswer != 2) {
        if (id % 2 == 1) {
          questionType.charAt(0) match {
            case '1' => questions1(0)
            case '2' => questions1(1)
            case '3' => questions1(2)
            case '4' => questions1(3)
            case '5' => questions1(4)
            case '6' => questions1(5)
            case _ => "-------------------"
          }
        } else {
          questionType.charAt(0) match {
            case '1' => questions2(0)
            case '2' => questions2(1)
            case '3' => questions2(2)
            case '4' => questions2(3)
            case '5' => questions2(4)
            case '6' => questions2(5)
            case _ => ""
          }
        }
      } else {
        if (id > correctAnswer) {
          questionType.charAt(0) match {
            case '1' => questions1(0)
            case '2' => questions1(1)
            case '3' => questions1(2)
            case '4' => questions1(3)
            case '5' => questions1(4)
            case '6' => questions1(5)
            case _ => "-------------------"
          }
        } else {
          questionType.charAt(0) match {
            case '1' => questions2(0)
            case '2' => questions2(1)
            case '3' => questions2(2)
            case '4' => questions2(3)
            case '5' => questions2(4)
            case '6' => questions2(5)
            case _ => ""
          }
        }
      }
    }

    def startTest(e: ReactEventI) = {

      val realTestQType = questionTypes.remove(0)
      val realTestApp = ReactComponentB[Unit]("RealSession")
        .initialState(MultiChoiceState(getRandomQuestion(testStrings, realTestQType), Cross(500),
          realTestQType, questionAmount, new Random().nextInt(3) + 1))
        .backend(getBackend(_))
        .render((_, S, B) => {
          val user = getElementById[Heading]("user")
          val userID: String = user.getAttribute("data-user-id")
          if (S.numberOfQuestions > 0) {
            S.whatToShow match {
              case Mask(_) => img(src := "/assets/images/mask.png", marginLeft := "auto", marginRight := "auto", display := "block")
              case Cross(_) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
              case ImageQ(_) => img(src := "/assets/images/multiChoice/" + S.res._1.imageType.split("_").mkString("/") + "/" + S.res._1.imageName + ".jpg", marginLeft := "auto", marginRight := "auto", display := "block")
              case ChoiceQuestion(_) => {
                div(
                  `class` := "bs-component",
                  p("Что было изображено на картинке?"),
                  form(
                    marginLeft := "auto",
                    marginRight := "auto",
                    `class` := "form-horizontal",
                    onSubmit ==> B.nextImage1,
                    button(
                      askQuestion(S.res._1.imageType, 1, S.correctAnswer),
                      `class` := "btn btn-primary",
                      marginLeft := "auto",
                      marginRight := "auto")
                  ),
                  p(),
                  form(
                    marginLeft := "auto",
                    marginRight := "auto",
                    `class` := "form-horizontal",
                    onSubmit ==> B.nextImage2,
                    button(askQuestion(S.res._1.imageType, 2, S.correctAnswer),
                      `class` := "btn btn-primary",
                      marginLeft := "auto",
                      marginRight := "auto")
                  ),
                  p(),
                  form(
                    marginLeft := "auto",
                    marginRight := "auto",
                    `class` := "form-horizontal",
                    onSubmit ==> B.nextImage3,
                    button(askQuestion(S.res._1.imageType, 3, S.correctAnswer),
                      `class` := "btn btn-primary",
                      marginLeft := "auto",
                      marginRight := "auto")

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
            js.timers.clearInterval(B.interval.get)
            submitReport(userID, addNoise(B.report.get.answers.toString))
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

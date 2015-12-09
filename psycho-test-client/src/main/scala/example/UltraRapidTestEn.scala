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

object UltraRapidTestEn {

  private val testQuestionAmount = 5
  private val questionAmount = 70
  private val testQuestionTypes = util.Random.shuffle(ArrayBuffer(1, 2, 3, 4, 5, 6))
  private val questionTypes = util.Random.shuffle(ArrayBuffer(1, 2, 3, 4, 5, 6))
  private val socialTestQuestionTypes = util.Random.shuffle(ArrayBuffer(7, 8))
  private val socialQuestionTypes = util.Random.shuffle(ArrayBuffer(7, 8))
  private val socialTestQuestionAmount = 5
  private val socialQuestionAmount = 40
  private var backend: scala.Option[Backend] = None
  private val question = getElementById[Div]("ultra-rapid-en")
  private var interval: js.UndefOr[js.timers.SetIntervalHandle] =
    js.undefined

  private def getBackend(sc: BackendScope[_, State]): Backend = {
    backend match {
      case None => backend = Some(new Backend(sc, true, None))
      case Some(x) => {
        val b = new Backend(sc, true, Some(x.report.get))
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
      newChild.src = "/assets/images/ultraRapid/" + image.imageName + ".jpg"
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
    val crossGreen = dom.document.createElement("img").asInstanceOf[Image]
    crossGreen.src = "/assets/images/cross-correct.png"
    getElementById[Div]("preload-div").appendChild(crossGreen)
    val crossRed = dom.document.createElement("img").asInstanceOf[Image]
    crossRed.src = "/assets/images/cross-incorrect.png"
    getElementById[Div]("preload-div").appendChild(crossRed)
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

    def askQuestion(questionType: Int): ReactElement = {
      questionType match {
        case 1 => customP("Is there a dog in this scene?")
        case 2 => customP("Is there an animal in this scene?")
        case 3 => customP("Is there a car in this scene?")
        case 4 => customP("Is there a vehicle in this scene?")
        case 5 => customP("Is this scene natural?")
        case 6 => customP("Is this scene artificial (man-made)?")
        case 7 => customP("Is this scene happening indoor?")
        case 8 => customP("Is there a positive interaction (friendship) present in this scene?")
        case _ => p("We don't have any questions for that type!")
      }
    }

    def createSocialApp(testQType: Int) = {
      ReactComponentB[Unit]("RealSocialSession")
        .initialState(State(getRandomQuestion(testStrings, testQType), FixationCross(500, true), false,
        testQType, 0))
        .backend(getBackend(_))
        .render((_, S, B) => {
        val user = getElementById[Heading]("user")
        var userID: String = user.getAttribute("data-user-id")
        // TODO for testing purposes only
        if (userID.isEmpty) {
          userID = "123"
        }
        if (S.questionType > 0) {
          S.whatToShow match {
            case FixationCross(_, _) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case ImageQuestion(_, _) => img(src := "/assets/images/ultraRapid/" + S.res._1.imageName + ".jpg", width := 650, marginLeft := "auto", marginRight := "auto", display := "block")
            case TextQuestion(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) =>
                  if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
                    B.clicked = false
                    B.showPicture(socialQuestionTypes, socialQuestionAmount)
                  }
              }
              askQuestion(S.questionType)
            }
            case Rest(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) => {}
              }
              h1()
            }
          }
        } else {
          js.timers.clearInterval(B.interval.get)
          div(
            h4("Thank you for your time. Testing is now finished. Please, press the button Finish Test"),
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
        f.backend.init(f.state, socialQuestionTypes, socialQuestionAmount)
      })
        .buildU
    }

    def createTestSocialApp(testQType: Int) = {
      ReactComponentB[Unit]("TestSocialSession")
        .initialState(State(getRandomQuestion(testStrings, testQType), FixationCross(500, true), true,
        testQType, 0))
        .backend(getBackend(_))
        .render((_, S, B) => {
        if (S.questionType > 0) {
          S.whatToShow match {
            case FixationCross(_, _) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case CorrectAnswerCross(_, _) => img(src := "/assets/images/cross-correct.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case IncorrectAnswerCross(_, _) => img(src := "/assets/images/cross-incorrect.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case ImageQuestion(_, _) => img(src := "/assets/images/ultraRapid/" + S.res._1.imageName + ".jpg", width := 650, marginLeft := "auto", marginRight := "auto", display := "block")
            case TextQuestion(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) =>
                  if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
                    B.clicked = false
                    B.showPicture(socialTestQuestionTypes, socialTestQuestionAmount)
                  }
              }
              askQuestion(S.questionType)
            }
            case Rest(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) => {}
              }
              h1()
            }
          }
        } else {
          js.timers.clearInterval(B.interval.get)
          val testQType = socialQuestionTypes.remove(0)
          val app = createSocialApp(testQType)
          val paragraph = getElementById[Paragraph]("countdown")
          paragraph.textContent = "Left: 120 seconds"
          var cnt = 120
          interval = js.timers.setInterval(1000)({
            if (cnt < 0) {
              paragraph.textContent = "_____"
              clearInterval
              React.render(app.apply(), question)
            } else {
              paragraph.textContent = "Осталось: " + cnt + " секунд"
              cnt -= 1
            }
          })
          h4("Attention! " +
            "Basic series of experiment will start soon. Recall the instruction. You will see a fixation cross, " +
            "and after that the image will appear for a fractions of seconds. ", br, br,
            "After that you will see a question about the content of the image (sample question: Is the scene happening indoor?)", br, br,
            "f your answer to this question is yes - click \"space\" immediately after the issue," +
              "if your answer is no - wait for the next task, namely the appearance of the fixation cross. ", br, br,
            "The test will continue in 2 minutes.")
        }
      })
        .componentDidMount(f => {
        f.backend.init(f.state, socialTestQuestionTypes, socialTestQuestionAmount)
      })
        .buildU
    }

    def startTest(e: ReactEventI) = {

      val realTestQType = questionTypes.remove(0)
      val realTestApp = ReactComponentB[Unit]("RealSession")
        .initialState(State(getRandomQuestion(testStrings, realTestQType), FixationCross(500, false), false,
        realTestQType, 0))
        .backend(getBackend(_))
        .render((_, S, B) => {
        if (S.questionType > 0) {
          S.whatToShow match {
            case FixationCross(_, _) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case ImageQuestion(_, _) => img(src := "/assets/images/ultraRapid/" + S.res._1.imageName + ".jpg", marginLeft := "auto", marginRight := "auto", display := "block")
            case TextQuestion(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) =>
                  if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
                    B.clicked = false
                    B.showPicture(questionTypes, questionAmount)
                  }
              }
              askQuestion(S.questionType)
            }
            case Rest(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) => {}
              }
              h1()
            }
          }
        } else {
          js.timers.clearInterval(B.interval.get)
          val testQType = socialTestQuestionTypes.remove(0)
          val app = createTestSocialApp(testQType)
          val paragraph = getElementById[Paragraph]("countdown")
          paragraph.textContent = "Left: 120 seconds"
          var cnt = 120
          interval = js.timers.setInterval(1000)({
            if (cnt < 0) {
              paragraph.textContent = "_____"
              clearInterval
              React.render(app.apply(), question)
            } else {
              paragraph.textContent = "Left: " + cnt + " seconds"
              cnt -= 1
            }
          })
          h4("Instruction to the second part of the experiment." +
            "The experiment consists of 90 tasks, which are divided into training and basic series. " +
            "In training you will be asked to perform 10 tasks with feedback about your success. ", br, br,
            "You will see a fixation cross, and after that will image on a few milliseconds, which quickly disappears. " +
              " After that you will see a question about the content of the image (sample question: Is the scene happening indoor?) ", br, br,
            "If your answer to this question is yes - click \"space\" immediately after the issue, " +
              "if your answer is no - wait for the next task - the appearance of the fixation cross. ", br, br,
            "The correctness of your response will be reflected in color of the fixation cross." +
              "E.g. the red cross - your answer was wrong, if the cross is green - you answered correctly.", br, br,
            " After the training begins the main series, its structure is identical to the training, " +
              "but it will not be given any feedback on the correctness of your answers. If you are ready, click Start.", br, br,
            "The test will continue in 2 minutes.")
        }
      })
        .componentDidMount(f => {
        f.backend.init(f.state, questionTypes, questionAmount)
      })
        .buildU

      val testQType = testQuestionTypes.remove(0)
      val testApp = ReactComponentB[Unit]("TestSession")
        .initialState(State(getRandomQuestion(testStrings, testQType), FixationCross(500, false), true,
        testQType, 0))
        .backend(sc => getBackend(sc))
        .render((_, S, B) => {
        if (S.questionType > 0) {
          S.whatToShow match {
            case FixationCross(_, _) => img(src := "/assets/images/cross.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case CorrectAnswerCross(_, _) => img(src := "/assets/images/cross-correct.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case IncorrectAnswerCross(_, _) => img(src := "/assets/images/cross-incorrect.png", marginLeft := "auto", marginRight := "auto", display := "block")
            case ImageQuestion(_, _) => img(src := "/assets/images/ultraRapid/" + S.res._1.imageName + ".jpg", marginLeft := "auto", marginRight := "auto", display := "block")
            case TextQuestion(_, _) => {
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) =>
                  if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
                    B.clicked = false
                    B.showPicture(testQuestionTypes, testQuestionAmount)
                  }
              }
              askQuestion(S.questionType)
            }
            case Rest(_, _) => {
              // reduce number of questions to be asked for this type of a question
              dom.document.onkeypress = {
                (e: dom.KeyboardEvent) => {}
              }
              h1()
            }
          }
        } else {
          js.timers.clearInterval(B.interval.get)
          val paragraph = getElementById[Paragraph]("countdown")
          paragraph.textContent = "Left: 120 seconds"
          var cnt = 120
          interval = js.timers.setInterval(1000)({
            if (cnt < 0) {
              paragraph.textContent = "_____"
              clearInterval
              React.render(realTestApp(), question)
            } else {
              paragraph.textContent = "Left: " + cnt + " seconds"
              cnt -= 1
            }
          })
          h4("Attention! Basic series of experiment will start soon. Recall the instruction: ", br, br,
            "You will see a fixation cross, and after that appears image for fractions of seconds, " +
              "that quickly disappear. After that you will see a question about the content of the image " +
              "(sample question: Is the scene artificial (man-made))? ", br, br,
            "If your answer to this question is yes - click \"space\" immediately after the question appears, " +
              "if your answer is no - wait for the next task, namely the appearance of the fixation cross. ", br, br,
            "The test will continue in 2 minutes.")
        }
      })
        .componentDidMount(f => {
        f.backend.init(f.state, testQuestionTypes, testQuestionAmount)
      })
        .buildU
      React.render(testApp(), question)
      getElementById[Div]("instruction").innerHTML = "<p id=\"countdown\">_____</p>"
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

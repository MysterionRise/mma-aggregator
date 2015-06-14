package example

import org.scalajs.dom
import org.scalajs.dom.html._
import example.ScalaJSCode._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import shared.{SharedCode, UltraRapidImage}
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js

object UltraRapidTest {

  private var testingStarted = false
  private var notClicked = true
  private var questionType = 1

  private val topMargin = 200

  /**
   *
   * @param image - current image, that we want to show
   * @param whatToShow - type of showing (fixation cross, question image, text question, rest)
   * @param isTesting - boolean flag representing test session or not
   * @param images - list of images
   * @param questionType - type of question
   *                     1 - is it a dog?
   *                     2 - is it animal?
   *                     3 - is it car?
   *                     4 - is it vehicle?
   *                     5 - is it nature?
   *                     6 - is it urban?
   */
  case class State(image: UltraRapidImage, whatToShow: WhatToShow, isTesting: Boolean, images: ArrayBuffer[UltraRapidImage], questionType: Int)

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

    def getCorrectAnswerByName(imageName: String, questionType: Int): Boolean = {
      imageName.startsWith(String.valueOf(questionType))
    }

    def extractImageType(image: UltraRapidImage): Int = Integer.parseInt(image.imageType)

    def showPicture(): Unit =
      stateController.modState(s => {
        s.whatToShow match {
          case r: Rest => {
            val next = r.moveToNext(fromBooleanToInt(s.isTesting))
            clearAndSetInterval(interval, next.getDuration)
            val idx = SharedCode.generateRandomValue(s.images.size)
            State(s.images.remove(idx), next, s.isTesting, s.images.result(), s.questionType)
          }
          case t: TextQuestion => {
            if (s.isTesting) {
              var nextState: WhatToShow = null
              val correctAnswer = getCorrectAnswerByName(s.image.imageType, questionType)
              if (notClicked && !correctAnswer) {
                report.addAnswerToReport(extractImageType(s.image), 1, questionType)
                nextState = t.moveToNext(1)
              } else if (!notClicked && correctAnswer) {
                report.addAnswerToReport(extractImageType(s.image), 2, questionType)
                nextState = t.moveToNext(1)
              } else {
                report.addAnswerToReport(extractImageType(s.image), 3, questionType)
                nextState = t.moveToNext(0)
              }
              notClicked = true
              clearAndSetInterval(interval, nextState.getDuration)
              State(s.image, nextState, s.isTesting, s.images, s.questionType)
            } else {
              val nextState = t.moveToNext(2)
              clearAndSetInterval(interval, nextState.getDuration)
              State(s.image, nextState, s.isTesting, s.images, s.questionType)
            }
          }
          case w: WhatToShow => {
            val nextState = w.moveToNext(fromBooleanToInt(s.isTesting))
            clearAndSetInterval(interval, nextState.getDuration)
            State(s.image, nextState, s.isTesting, s.images, s.questionType)
          }
        }
      })

    def init(state: State) = {
      dom.document.cookie = ""
      interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture())
    }
  }

  def constructArrayBuffer(s: String) = {
    val res = new ArrayBuffer[UltraRapidImage]()
    val pairs = s.split(";")
    for (pair <- pairs) {
      res.append(UltraRapidImage(pair.split(",")(0), pair.split(",")(1)))
    }
    // todo take only 10 first elements
    util.Random.shuffle(res).take(20)
  }

  private lazy val testStrings = constructArrayBuffer(getElementById[Div]("images").getAttribute("data-images"))

  def customP(innerText: String): ReactElement = {
    h2(
      p(
        marginTop := topMargin,
        marginLeft := topMargin,
        innerText
      )
    )
  }

  val testApp = ReactComponentB[Unit]("TestSession")
    .initialState(State(testStrings.remove(SharedCode.generateRandomValue(testStrings.size)), FixationCross(500), true, testStrings, questionType))
    .backend(new Backend(_))
    .render((_, S, B) => {
    // todo fix bug with last element
    if (!S.images.isEmpty) {
      S.whatToShow match {
        case FixationCross(_) => img(src := "/assets/images/cross.png")
        case CorrectAnswerCross(_) => img(src := "/assets/images/cross-correct.png")
        case IncorrectAnswerCross(_) => img(src := "/assets/images/cross-incorrect.png")
        case ImageQuestion(_) => img(src := "/assets/images/ultraRapid/" + S.image.imageName)
        case TextQuestion(_) => {
          dom.document.onkeypress = {
            (e: dom.KeyboardEvent) =>
              if (e.charCode == 32 && S.whatToShow.isInstanceOf[TextQuestion]) {
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
          // TODO ask proper question
          questionType match {
            case 1 => customP("Did you see dog here?")
            case _ => p("We don't have any questions for that type!")
          }
        }
        case Rest(_) => {
          dom.document.onkeypress = {
            (e: dom.KeyboardEvent) => {}
          }
          h1()
          //          customP("Take a rest, please!")
        }
      }
    } else {
      js.timers.clearInterval(B.interval.get)
      div(
        form(
          action := "/tests/finishTest?report=\"" + B.report.toString + "\"",
          `class` := "form-horizontal",
          method := "POST",
          button(
            id := "finish-test",
            `type` := "submit",
            `class` := "btn btn-primary",
            "Finish Test"
          )
        )
      )
    }
  })
    .componentDidMount(f => {
    f.backend.init(f.state)
  })
    .buildU

  val question = getElementById[Div]("ultra-rapid")

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

  def doTest() = {
    React.render(buttonApp(), question)
  }

  class TestBackend($: BackendScope[_, String]) {
    def startTest(e: ReactEventI) = {
      React.render(testApp(), question)
      getElementById[Div]("instruction").innerHTML = ""
      $.setState("")
    }
  }

}

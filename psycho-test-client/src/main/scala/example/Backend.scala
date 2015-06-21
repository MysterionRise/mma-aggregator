package example

import example.ScalaJSCode._
import example.UltraRapidTest.State
import japgolly.scalajs.react.BackendScope
import org.scalajs.dom
import org.scalajs.dom.html._
import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js

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

  def clearAndSetInterval(interval: js.UndefOr[js.timers.SetIntervalHandle], duration: Int,
                          questionTypes: ArrayBuffer[Int], questionMargin: Int) = {
    js.timers.clearInterval(interval.get)
    this.interval = js.timers.setInterval(duration)(showPicture(questionTypes, questionMargin))
  }

  def fromBooleanToInt(b: Boolean): Int = if (b) 1 else 0

  def getCorrectAnswerByName(imageName: String, questionType: Int): Boolean = {
    imageName.startsWith(String.valueOf(questionType))
  }

  def extractImageType(image: UltraRapidImage): Int = Integer.parseInt(image.imageType)

  def showPicture(questionTypes: ArrayBuffer[Int], questionMargin: Int): Unit =
    stateController.modState(s => {
      s.whatToShow match {
        case r: Rest => {
          val next = r.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, next.getDuration, questionTypes, questionMargin)

          if (s.numberOfQuestions == questionMargin) {
            if (questionTypes.length == 0) {
              State(UltraRapidImage.apply("", ""), next, s.isTesting,
                s.images.result(), 0, 0)
            } else {
              val qType = questionTypes.remove(0)
              State(getRandomQuestion(s.images, qType), next, s.isTesting,
                s.images.result(), qType, 0)
            }
          } else {
            State(getRandomQuestion(s.images, s.questionType), next, s.isTesting,
              s.images.result(), s.questionType, s.numberOfQuestions + 1)
          }

        }
        case t: TextQuestion => {
          if (s.isTesting) {
            var nextState: WhatToShow = null
            val correctAnswer = getCorrectAnswerByName(s.image.imageType, s.questionType)
            if (notClicked && !correctAnswer) {
              nextState = t.moveToNext(1)
            } else if (!notClicked && correctAnswer) {
              nextState = t.moveToNext(1)
            } else {
              nextState = t.moveToNext(0)
            }
            notClicked = true
            clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
            State(s.image, nextState, s.isTesting, s.images, s.questionType, s.numberOfQuestions)
          } else {
            val nextState = t.moveToNext(2)
            val correctAnswer = getCorrectAnswerByName(s.image.imageType, s.questionType)
            clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
            if (notClicked && !correctAnswer) {
              report.addAnswerToReport(extractImageType(s.image), 1, s.questionType)
            } else if (!notClicked && correctAnswer) {
              report.addAnswerToReport(extractImageType(s.image), 2, s.questionType)
            } else {
              report.addAnswerToReport(extractImageType(s.image), 3, s.questionType)
            }
            notClicked = true
            State(s.image, nextState, s.isTesting, s.images, s.questionType, s.numberOfQuestions)
          }
        }
        case w: WhatToShow => {
          val nextState = w.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          State(s.image, nextState, s.isTesting, s.images, s.questionType, s.numberOfQuestions)
        }
      }
    })

  def init(state: State, questionTypes: ArrayBuffer[Int], questionMargin: Int) = {
    dom.document.cookie = ""
    interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture(questionTypes, questionMargin))
  }
}
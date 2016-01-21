package example

import example.ScalaJSCode._
import example.UltraRapidTest._
import japgolly.scalajs.react.BackendScope
import org.scalajs.dom
import org.scalajs.dom.html._
import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js

class Backend2(stateController: BackendScope[_, State], var clicked: Boolean, var report: scala.Option[Report]) {

  val user = getElementById[Heading]("user")
  val userID: String = user.getAttribute("data-user-id")
  var interval: js.UndefOr[js.timers.SetIntervalHandle] =
    js.undefined

  def clearAndSetInterval(interval: js.UndefOr[js.timers.SetIntervalHandle], duration: Int,
                          questionTypes: ArrayBuffer[Int], questionMargin: Int): Unit = {
    js.timers.clearInterval(interval.get)
    this.interval = js.timers.setInterval(duration)(showPicture(questionTypes, questionMargin))
  }

  def clearInterval(interval: js.UndefOr[js.timers.SetIntervalHandle]): Unit = {
    js.timers.clearInterval(interval.get)
  }

  def fromBooleanToInt(b: Boolean): Int = if (b) 1 else 0

  def getCorrectAnswerByName(imageType: String, imageName: String, questionType: Int): Boolean = {
    if (questionType == 7) {
      imageName.split("\\.")(0).split("_")(1).equals("1")
    } else if (questionType == 8) {
      imageName.split("\\.")(0).split("_")(2).equals("1")
    } else {
      imageType.startsWith(String.valueOf(questionType))
    }
  }

  def extractImageType(image: UltraRapidImage): Int = Integer.parseInt(image.imageType)

  def showPicture(questionTypes: ArrayBuffer[Int], questionMargin: Int) =
    stateController.modState(s => {
      s.whatToShow match {
        case r: Rest => {
          val next = r.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, next.getDuration, questionTypes, questionMargin)
          if (s.numberOfQuestions == questionMargin) {
            if (questionTypes.length == 0) {
              State((UltraRapidImage("", "", false), s.res._2), next, s.isTesting,
                0, 0)
            } else {
              val qType = questionTypes.remove(0)
              State(getRandomQuestion(s.res._2, qType), next, s.isTesting,
                qType, 0)
            }
          } else {
            State(getRandomQuestion(s.res._2, s.questionType), next, s.isTesting,
              s.questionType, s.numberOfQuestions + 1)
          }
        }
        case t: TextQuestion => {
          if (s.isTesting) {
            clearInterval(interval)
            StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, true)
          } else {
            clearInterval(interval)
            StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, true)

          }
        }
        case n: NoNextState => {
          StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, s.isVersion2)
        }
        case w: WhatToShow => {
          val nextState = w.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          State(s.res, nextState, s.isTesting, s.questionType, s.numberOfQuestions)
        }
      }
    })

  def init(state: State, questionTypes: ArrayBuffer[Int], questionMargin: Int) = {
    dom.document.cookie = ""
    report match {
      case None => report = Some(new Report(userID))
      case _ =>
    }
    interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture(questionTypes, questionMargin))
  }
}
package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import org.scalajs.dom
import org.scalajs.dom.html._
import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.util.Random

class Backend2(stateController: BackendScope[_, State], var clicked: Boolean, var report: scala.Option[Report2]) {

  var res: String = ""
  var questionId: Int = 0
  var time: Long = 0

  def addText(e: ReactEventI) = {
    res = e.target.value
  }

  def nextImage(e: ReactEventI): Unit = {
    e.preventDefault()
    val next = new Rest(new Random().nextInt(1500) + 500, false)
    stateController.modState(s => {
      clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
      State((null, s.res._2),
        next, s.isTesting,
        s.questionType, s.numberOfQuestions)
    })
  }

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

  def extractImageType(image: UltraRapidImage): Int = Integer.parseInt(image.imageType)

  def showPicture(questionTypes: ArrayBuffer[Int], questionMargin: Int) =
    stateController.modState(s => {
      s.whatToShow match {
        case r: Rest => {
          report.get.addAnswerToReport(questionId, res, System.currentTimeMillis() - time)
          res = ""
          val next = r.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, next.getDuration, questionTypes, questionMargin)
          if (!s.res._2.isEmpty)
            State(UltraRapid2Test.getRandomQuestion(s.res._2), next, s.isTesting,
              s.questionType, s.numberOfQuestions - 1)
          else
            State((null, null), next, s.isTesting,
              s.questionType, -1)
        }
        case t: TextQuestion => {
          clearInterval(interval)
          StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, true)
        }
        case n: NoNextState => {
          StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, s.isVersion2)
        }
        case w: WhatToShow => {
          time = System.currentTimeMillis()
          questionId = Integer.valueOf(s.res._1.imageName)
          val nextState = w.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          State(s.res, nextState, s.isTesting, s.questionType, s.numberOfQuestions)
        }
      }
    })

  def init(state: State, questionTypes: ArrayBuffer[Int], questionMargin: Int) = {
    dom.document.cookie = ""
    report match {
      case None => report = Some(new Report2(userID))
      case _ =>
    }
    interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture(questionTypes, questionMargin))
  }
}
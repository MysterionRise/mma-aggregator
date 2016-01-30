package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import org.scalajs.dom
import org.scalajs.dom.html._
import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.util.Random

class Backend2(stateController: BackendScope[_, State], var clicked: Boolean, var report: scala.Option[Report]) {

  def nextImage(e: ReactEventI): Unit = {
    println("next image")
    val next = new Rest(new Random().nextInt(1500) + 500, false)
    stateController.modState(s => {
      clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
      State(UltraRapid2Test.getRandomQuestion(s.res._2),
        next, s.isTesting,
        s.questionType, s.numberOfQuestions + 1)
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
          println("rest")
          val next = r.moveToNext(fromBooleanToInt(s.isTesting))
          clearAndSetInterval(interval, next.getDuration, questionTypes, questionMargin)
          println(s.res._2.length)
          State(UltraRapid2Test.getRandomQuestion(s.res._2), next, s.isTesting,
            s.questionType, s.numberOfQuestions + 1)
        }
        case t: TextQuestion => {
          println("text question")
          clearInterval(interval)
          StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, true)
        }
        case n: NoNextState => {
          println("no next state")
          StateObj.apply(s.res, NoNextState(-1), s.isTesting, s.questionType, s.numberOfQuestions, s.isVersion2)
        }
        case w: WhatToShow => {
          println(w)
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
package example

import example.ScalaJSCode._
import japgolly.scalajs.react._
import org.scalajs.dom
import org.scalajs.dom.html._
import shared.UltraRapidImage

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.util.Random

class MultiChoiceBackend(stateController: BackendScope[_, MultiChoiceState], var clicked: Boolean, var report: scala.Option[Report2]) {

  var res: String = ""
  var questionId: Int = 0
  var time: Long = 0
  var debugTime: Long = 0
  var debugtime: Long = 0
  var currentInterval: Int = 0
  val random = new Random()
  var currentImageDuration = 33
  var currentImageDurationInd = 0
  val durations = List(33, 53, 80, 105, 500)
  var correctAnswer1 = false
  var correctAnswer2 = false
  var correctAnswer3 = false

  def addText(e: ReactEventI) = {
    res = e.target.value
  }

  def nextImage1(e: ReactEventI): Unit = {
    e.preventDefault()
    stateController.modState(s => {
      //      s.res._1.imageType.charAt(0) match {
      //        case '1' => correctAnswer = true
      //        case '2' => correctAnswer = true
      //        case '3' => correctAnswer = true
      //        case '4' => correctAnswer = true
      //        case '5' => correctAnswer = true
      //        case '6' => correctAnswer = true
      //        case _ =>
      //      }
      if (correctAnswer1) {
        val next = new RestPeriod(random.nextInt(1500) + 500)
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((null, s.res._2),
          next,
          s.questionType, s.numberOfQuestions)
      } else {
        val next = new Cross(500)
        if (currentImageDurationInd < 4)
          currentImageDurationInd += 1
        next.setDuration(durations(currentImageDurationInd))
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((s.res._1, s.res._2),
          next,
          s.questionType, s.numberOfQuestions)
      }
    })
  }

  def nextImage2(e: ReactEventI): Unit = {
    e.preventDefault()
    stateController.modState(s => {
      if (correctAnswer2) {
        val next = new RestPeriod(random.nextInt(1500) + 500)
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((null, s.res._2),
          next,
          s.questionType, s.numberOfQuestions)
      } else {
        val next = new Cross(500)
        if (currentImageDurationInd < 4)
          currentImageDurationInd += 1
        next.setDuration(durations(currentImageDurationInd))
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((s.res._1, s.res._2),
          next,
          s.questionType, s.numberOfQuestions)
      }
    })
  }

  def nextImage3(e: ReactEventI): Unit = {
    e.preventDefault()
    stateController.modState(s => {
      if (correctAnswer3) {
        val next = new RestPeriod(random.nextInt(1500) + 500)
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((null, s.res._2),
          next,
          s.questionType, s.numberOfQuestions)
      } else {
        val next = new Cross(500)
        if (currentImageDurationInd < 4)
          currentImageDurationInd += 1
        next.setDuration(durations(currentImageDurationInd))
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((s.res._1, s.res._2),
          next,
          s.questionType, s.numberOfQuestions)
      }
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
        case r: RestPeriod => {
          currentImageDuration = 33
          currentImageDurationInd = 0
          report.get.addAnswerToReport(questionId, res, System.currentTimeMillis() - time, debugtime)
          res = ""
          val next = r.moveToNext()
          clearAndSetInterval(interval, next.getDuration, questionTypes, questionMargin)
          debugTime = System.currentTimeMillis()
          if (!s.res._2.isEmpty)
            MultiChoiceState(GlobalRecognitionTest.getRandomQuestion(s.res._2), next,
              s.questionType, s.numberOfQuestions - 1)
          else
            MultiChoiceState((null, null), next,
              s.questionType, -1)
        }
        case f: Cross => {
          time = System.currentTimeMillis()
          debugTime = System.currentTimeMillis()
          questionId = Integer.valueOf(s.res._1.imageName)
          val nextState = f.moveToNext()
          currentInterval = nextState.getDuration
          println(currentInterval)
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          MultiChoiceState(s.res, nextState, s.questionType, s.numberOfQuestions)
        }
        case n: ChoiceQuestion => {
          MultiChoiceState(s.res, ChoiceQuestion(-1), s.questionType, s.numberOfQuestions)
        }
        case w: WhatToShow2 => {
          time = System.currentTimeMillis()
          questionId = Integer.valueOf(s.res._1.imageName)
          val nextState = w.moveToNext()
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          MultiChoiceState(s.res, nextState, s.questionType, s.numberOfQuestions)
        }
      }
    })

  def init(state: MultiChoiceState, questionTypes: ArrayBuffer[Int], questionMargin: Int) = {
    dom.document.cookie = ""
    report match {
      case None => report = Some(new Report2(userID))
      case _ =>
    }
    interval = js.timers.setInterval(state.whatToShow.getDuration)(showPicture(questionTypes, questionMargin))
  }
}
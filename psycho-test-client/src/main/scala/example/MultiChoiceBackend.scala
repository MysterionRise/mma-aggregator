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

  var startTime: Long = 0
  var endTime: Long = 0
  var debugTime: Long = 0
  var currentInterval: Int = 0
  val random = new Random()
  var currentImageDurationInd = 0
  val durations = List(33, 53, 80, 105, 500)
  var questionId = ""

  def nextCorrectAnswer: Int = new Random().nextInt(3) + 1

  def nextImage1(e: ReactEventI): Unit = {
    e.preventDefault()
    stateController.modState(s => {
      if (s.correctAnswer == 1) {
        val next = new RestPeriod(random.nextInt(1500) + 500)
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((null, s.res._2),
          next,
          s.questionType, s.numberOfQuestions, nextCorrectAnswer)
      } else {
        val next = new Cross(500)
        if (currentImageDurationInd < 4)
          currentImageDurationInd += 1
        next.setDuration(durations(currentImageDurationInd))
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((s.res._1, s.res._2),
          next,
          s.questionType, s.numberOfQuestions, nextCorrectAnswer)
      }
    })
  }

  def nextImage2(e: ReactEventI): Unit = {
    e.preventDefault()
    stateController.modState(s => {
      if (s.correctAnswer == 2) {
        val next = new RestPeriod(random.nextInt(1500) + 500)
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((null, s.res._2),
          next,
          s.questionType, s.numberOfQuestions, nextCorrectAnswer)
      } else {
        val next = new Cross(500)
        if (currentImageDurationInd < 4)
          currentImageDurationInd += 1
        next.setDuration(durations(currentImageDurationInd))
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((s.res._1, s.res._2),
          next,
          s.questionType, s.numberOfQuestions, nextCorrectAnswer)
      }
    })
  }

  def nextImage3(e: ReactEventI): Unit = {
    e.preventDefault()
    stateController.modState(s => {
      if (s.correctAnswer == 3) {
        val next = new RestPeriod(random.nextInt(1500) + 500)
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((null, s.res._2),
          next,
          s.questionType, s.numberOfQuestions, nextCorrectAnswer)
      } else {
        val next = new Cross(500)
        if (currentImageDurationInd < 4)
          currentImageDurationInd += 1
        next.setDuration(durations(currentImageDurationInd))
        clearAndSetInterval(interval, next.getDuration, new ArrayBuffer[Int](), s.res._2.length)
        MultiChoiceState((s.res._1, s.res._2),
          next,
          s.questionType, s.numberOfQuestions, nextCorrectAnswer)
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
          report.get.addAnswerToReport(questionId, (endTime - startTime).toString, durations(currentImageDurationInd), System.currentTimeMillis() - debugTime)
          currentImageDurationInd = 0
          val next = r.moveToNext()
          clearAndSetInterval(interval, next.getDuration, questionTypes, questionMargin)
          debugTime = System.currentTimeMillis()
          if (!s.res._2.isEmpty)
            MultiChoiceState(GlobalRecognitionTest.getRandomQuestion(s.res._2), next,
              s.questionType, s.numberOfQuestions - 1, s.correctAnswer)
          else
            MultiChoiceState((null, null), next,
              s.questionType, -1, s.correctAnswer)
        }
        case f: Cross => {
          debugTime = System.currentTimeMillis()
          startTime = System.currentTimeMillis()
          questionId = s.res._1.imageType
          val nextState = f.moveToNext()
          currentInterval = nextState.getDuration
          println(currentInterval)
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          MultiChoiceState(s.res, nextState, s.questionType, s.numberOfQuestions, s.correctAnswer)
        }
        case i: ImageQ => {
          endTime = System.currentTimeMillis()
          val nextState = i.moveToNext()
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          MultiChoiceState(s.res, nextState, s.questionType, s.numberOfQuestions, s.correctAnswer)
        }
        case m: Mask => {
          val nextState = m.moveToNext()
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          MultiChoiceState(s.res, nextState, s.questionType, s.numberOfQuestions, s.correctAnswer)
        }
        case n: ChoiceQuestion => {
          MultiChoiceState(s.res, ChoiceQuestion(-1), s.questionType, s.numberOfQuestions, s.correctAnswer)
        }
        case w: WhatToShow2 => {
          questionId = s.res._1.imageType
          val nextState = w.moveToNext()
          clearAndSetInterval(interval, nextState.getDuration, questionTypes, questionMargin)
          MultiChoiceState(s.res, nextState, s.questionType, s.numberOfQuestions, s.correctAnswer)
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
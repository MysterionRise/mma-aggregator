package example

import scala.collection.mutable.ArrayBuffer

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
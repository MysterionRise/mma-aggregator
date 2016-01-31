package example

import scala.collection.mutable.ArrayBuffer

class Report2(userName: String) {
  val answers = new ArrayBuffer[(Int, String, Long, Long)]()

  def addAnswerToReport(questionId: Int, response: String, time: Long, debugtime: Long) = {
    answers += ((questionId, response, time, debugtime))
  }

  def createReport(answers: ArrayBuffer[(Int, String, Long, Long)]): String = {
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
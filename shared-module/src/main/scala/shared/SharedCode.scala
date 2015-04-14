package shared

import scala.collection.mutable

object SharedCode {
  val reports = new mutable.HashMap[String, String]()

  def saveToDB(userID: String, imageID: Int, roundNumber: Int, time: Long) = {
    reports.put(userID ,s""" ${userID} clicked on image ${imageID} in round ${roundNumber}""")
  }

  def getReport(userID: String): String = {
    reports.getOrElse(userID, "Empty report!")
  }

  def constructImage(s: String): Image = {
    val paths = s.split("/")
    val len = paths.length
    return new Image(paths(len - 3), Integer.parseInt(paths(len - 2)), Integer.parseInt(paths(len - 1).split("\\.")(0)))
  }

  def constructSrc(prefix: String, t: Image): String = {
    return prefix + "/" + t.testName + "/" + t.roundNumber + "/" + t.imageNumber + ".jpg"
  }

  def getPrefix(s: String): String = {
    val paths = s.split("/")
    val len = paths.length
    var res = ""
    for (i <- 0 until len - 3) {
      res += paths(i) + "/"
    }
    // replace with fold construction
    return res
  }

  def constructURI(t: Image): String = {
    return constructSrc("images", t)
  }
}

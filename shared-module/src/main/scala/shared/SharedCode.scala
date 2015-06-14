package shared

import scala.collection.mutable.ArrayBuffer

object SharedCode {

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

  def generateImages: ArrayBuffer[UltraRapidImage] = {
    // TODO read files
    ArrayBuffer("n_1", "n_2", "n_3", "n_51", "n_52", "n_53", "y_316", "y_317", "y_318", "y_319", "y_320")
  }
}

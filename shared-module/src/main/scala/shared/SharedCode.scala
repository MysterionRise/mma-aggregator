package shared

import scala.util.Random

object SharedCode {

  val random = new Random()

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

  /**
   * @return random number from 1 to n, inclusively
   */
  def generateRandomValue(n: Int) = random.nextInt(n + 1) + 1

  /**
   * @return random index for array of length = len
   */
  def generateRandomIndex(len: Int) = random.nextInt(len)
}

package example

import scala.util.Random

abstract class WhatToShow(duration: Int) {

  def getDuration = this.duration
  def moveToNext(): WhatToShow
}

case class FixationCross(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(): WhatToShow = new ImageQuestion(53)
}

case class ImageQuestion(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(): WhatToShow = new TextQuestion(1000)
}

case class TextQuestion(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(): WhatToShow = new Rest(new Random().nextInt(1000) + 500)
}

case class Rest(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(): WhatToShow = new FixationCross(500)
}
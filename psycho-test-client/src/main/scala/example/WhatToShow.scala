package example

import scala.util.Random

abstract class WhatToShow(duration: Int) {

  def getDuration = this.duration

  def moveToNext(param: Int): WhatToShow
}

case class FixationCross(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new ImageQuestion(153)
}

case class ImageQuestion(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new TextQuestion(1000)
}

case class TextQuestion(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = {
    param match {
      case 0 => new IncorrectAnswerCross(1000)
      case 1 => new CorrectAnswerCross(1000)
      case _ => new Rest(new Random().nextInt(1500) + 500)
    }
  }
}

case class Rest(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new FixationCross(500)
}

case class CorrectAnswerCross(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new Rest(new Random().nextInt(1500) + 500)
}

case class IncorrectAnswerCross(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new Rest(new Random().nextInt(1500) + 500)
}
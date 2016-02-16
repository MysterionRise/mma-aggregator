package example

abstract class WhatToShow(duration: Int) {

  def getDuration = this.duration

  def moveToNext(param: Int): WhatToShow
}

case class FixationCross(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = if (isSocial) new ImageQuestion(83, true) else new ImageQuestion(33, false)
}

case class ImageQuestion(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new TextQuestion(1000, isSocial)
}

case class TextQuestion(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = {
    param match {
      case 0 => new IncorrectAnswerCross(1000, isSocial)
      case 1 => new CorrectAnswerCross(1000, isSocial)
      case _ => new Rest(1500, isSocial)
    }
  }
}

case class Rest(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new FixationCross(500, isSocial)
}

case class CorrectAnswerCross(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new Rest(1500, isSocial)
}

case class IncorrectAnswerCross(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = new Rest(1500, isSocial)
}

case class NoNextState(duration: Int) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = NoNextState(-1)
}

case class Mask(duration: Int, isSocial: Boolean) extends WhatToShow(duration) {
  override def moveToNext(param: Int): WhatToShow = ???
}
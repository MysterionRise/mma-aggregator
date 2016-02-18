package example

abstract class WhatToShow2(duration: Int) {

  def getDuration = this.duration

  def moveToNext(param: Int): WhatToShow2
}

// todo add choosing times from list based on smth
case class Cross(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(param: Int): WhatToShow2 = Image(5)
}

case class Image(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(param: Int): WhatToShow2 = new ChoiceQuestion(-1)
}

case class Rest(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(param: Int): WhatToShow2 = new Cross(500)
}

case class ChoiceQuestion(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(param: Int): WhatToShow2 = ChoiceQuestion(-1)
}

// TODO choose time for mask
case class Mask(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(param: Int): WhatToShow2 = ???
}
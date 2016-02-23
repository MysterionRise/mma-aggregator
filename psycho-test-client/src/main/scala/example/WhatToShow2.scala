package example

abstract class WhatToShow2(duration: Int) {

  def getDuration = this.duration

  def moveToNext(): WhatToShow2
}

// todo add choosing times from list based on smth
case class Cross(duration: Int) extends WhatToShow2(duration) {
  def setDuration(duration: Int) = {
    currentDuration = duration
  }

  var currentDuration = 33

  override def moveToNext(): WhatToShow2 = ImageQ(currentDuration)
}

case class ImageQ(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(): WhatToShow2 = new Mask(500)
}

case class RestPeriod(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(): WhatToShow2 = new Cross(500)
}

case class ChoiceQuestion(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(): WhatToShow2 = ChoiceQuestion(-1)
}

case class Mask(duration: Int) extends WhatToShow2(duration) {
  override def moveToNext(): WhatToShow2 = ChoiceQuestion(-1)
}
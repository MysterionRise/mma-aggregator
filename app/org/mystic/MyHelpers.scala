package org.mystic

import views.html.helper.FieldConstructor

object MyHelpers {
  implicit val myFields = FieldConstructor(views.html.helper.bootstrapInput.f)
}

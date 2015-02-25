package controllers

import models.Person
import play.api.mvc.{Action, Controller}

object Users extends Controller {
  def people() = Action {
    val people = Person.getAll
    Ok(views.html.people(people))
  }
}
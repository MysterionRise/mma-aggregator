package controllers

import models.{TestDAO, SubscriptionDAO, UserDAO}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    TestDAO.createSchema
    SubscriptionDAO.createSchema
    UserDAO.createSchema
    Ok(views.html.index("Your new application is ready."))
  }

  def users() = Action {
    Ok(views.html.subscriptions(UserDAO.findAll))
  }

  def tests() = Action {
    Ok(views.html.tests(TestDAO.findAll, PsychoTest.testForm))
  }
}
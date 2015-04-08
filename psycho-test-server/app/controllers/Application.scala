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
    Ok(views.html.index("Go to /tests and start to check all tests we have!"))
  }

  def users() = Action {
    Ok(views.html.subscriptions(UserDAO.findAll))
  }

  def tests() = Action {
    Ok(views.html.tests(TestDAO.findAll, PsychoTest.testForm))
  }
}
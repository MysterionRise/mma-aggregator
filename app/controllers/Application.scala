package controllers

import models.{TestDAO, SubscriptionDAO, UserDAO}
import play.api.mvc._


object Application extends Controller {

  def index = Action {
    TestDAO.createSchema
    SubscriptionDAO.createSchema
    UserDAO.createSchema
    Ok(views.html.index("Your new application is ready."))
  }

  def subscriptions() = Action {
    Ok(views.html.subscriptions(SubscriptionDAO.findAll))
  }

  def tests() = Action {
    Ok(views.html.tests(TestDAO.findAll))
  }
}
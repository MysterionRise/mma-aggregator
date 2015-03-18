package controllers

import models.{UserDAO, SubscriptionDAO}
import play.api.mvc._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def subscriptions() = Action {
    Ok(views.html.subscriptions(SubscriptionDAO.findAll))
  }

  def users() = Action {
    Ok(views.html.kagan(UserDAO.findAll))
  }
}
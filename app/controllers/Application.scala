package controllers

import models.SubscriptionDAO
import play.api.mvc._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def subscriptions() = Action {
    Ok(views.html.subscriptions(SubscriptionDAO.findAll))
  }

  def kagan() = Action {
    Ok(views.html.kagan())
  }
}
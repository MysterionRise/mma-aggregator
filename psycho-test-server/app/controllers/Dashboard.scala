package controllers

import models.ReportDAO
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global


class Dashboard extends Controller {


  def dashboard = Action.async {
    ReportDAO.getReports.map(res => Ok(views.html.dashboard(res)))
  }

}

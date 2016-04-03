package controllers

import java.util.concurrent.TimeUnit

import models.ReportDAO
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

object Dashboard extends Controller with Secured {

  def dashboard = IsAuthenticated { username =>
    implicit request =>
      Await.result(ReportDAO.getReports.map(res => Ok(views.html.dashboard(res))), FiniteDuration.apply(10, TimeUnit.SECONDS))
  }

  /**
    * Redirect to login if the use in not authorized.
    */
  override def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login())

}

package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.Future

object Global extends GlobalSettings {
  override def onHandlerNotFound(request: RequestHeader): Future[SimpleResult] = Future.successful(
    NotFound(views.html.notFound(request.path))
  )
}
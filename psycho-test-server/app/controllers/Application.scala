package controllers

import models.{ReportDAO, TestDAO, UserDAO}
import play.api.mvc._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import be.objectify.deadbolt.scala.DeadboltActions

//@Inject()(deadbolt: DeadboltActions)

class Application extends Controller {

  def index = Action {
    TestDAO.createSchema
    ReportDAO.createSchema
    UserDAO.createSchema
    Ok(views.html.index("Go to /tests and start to check all tests we have!"))
  }

  def tests = Action.async {
    TestDAO.getDB.run(TestDAO.tests.result).map(res => Ok(views.html.tests(res.toList, PsychoTest.testForm)))
  }

  def dashboard = Action.async {
    ReportDAO.getReports.map(res => Ok(views.html.dashboard(res)))
  }
}
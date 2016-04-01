package controllers

import models.{ReportDAO, TestDAO, UserDAO}
import play.api.mvc._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

import be.objectify.deadbolt.scala.DeadboltActions

//@Inject()(deadbolt: DeadboltActions)

object Application extends Controller {

  def index = Action {
    TestDAO.createSchema
    ReportDAO.createSchema
    UserDAO.createSchema
    Ok(views.html.index("Go to /tests and start to check all tests we have!"))
  }

  def tests = Action.async {
    TestDAO.getDB.run(TestDAO.tests.result).map(res => Ok(views.html.tests(res.toList, PsychoTest.testForm)))
  }
}
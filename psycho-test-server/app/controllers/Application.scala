package controllers

import javax.inject.Inject

import models.{ReportDAO, TestDAO, UserDAO}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject()(dbConfigProvider: DatabaseConfigProvider) extends Controller {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  def index = Action {
    TestDAO.createSchema
    ReportDAO.createSchema
    UserDAO.createSchema
    Ok(views.html.index("Go to /tests and start to check all tests we have!"))
  }

  def tests = Action.async {
    db.run(TestDAO.tests.result).map(res => Ok(views.html.tests(res.toList, PsychoTest.testForm)))
  }
}
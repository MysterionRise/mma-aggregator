package controllers

import models.{AuthUsersDAO, ReportDAO, TestDAO, UserDAO}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    TestDAO.createSchema
    ReportDAO.createSchema
    UserDAO.createSchema
    AuthUsersDAO.createSchema
    Ok(views.html.index("Go to /tests and start to check all tests we have!"))
  }

  def tests = Action.async {
    TestDAO.getDB.run(TestDAO.tests.result).map(res => Ok(views.html.tests(res.toList, PsychoTest.testForm)))
  }

  lazy val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying("Invalid user or password", result => result match {
      case (email, password) => {
//        println("user=" + email + " password=" + password);
        val userList = AuthUsersDAO.authenticate(email, password)
        println(userList)
        userList == 1
      }
      case _ => false
    }))

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
    * Handle login form submission.
    */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Dashboard.dashboard).withSession("email" -> user._1))
  }

  /**
    * Logout and clean the session.
    */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }
}

/**
  * Provide security features
  */
trait Secured {
  self: Controller =>

  /**
    * Retrieve the connected user id.
    */
  def username(request: RequestHeader) = request.session.get("email")

  /**
    * Redirect to login if the use in not authorized.
    */
  def onUnauthorized(request: RequestHeader): Result

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
}
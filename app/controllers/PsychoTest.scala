package controllers

import models.{UserDAO, User}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Call, Action}
import play.mvc.Controller
import play.api.mvc.Results._

case class UserReq(email: String, name: String, nationality: String, gender: String, age: Int)

object PsychoTest extends Controller {

  val userMapping =
    mapping(
      "email" -> email,
      "name" -> nonEmptyText(maxLength = 200),
      "nationality" -> nonEmptyText(maxLength = 200),
      "gender" -> nonEmptyText(maxLength = 200),
      "age" -> number(min = 0)
    )(UserReq.apply)(UserReq.unapply)

  val testForm: Form[UserReq] = Form(userMapping)

  def startTest() = Action { implicit req =>
    testForm.bindFromRequest.fold(
      errors =>
        BadRequest(views.html.tests(List.empty, errors)),
      user => {
        UserDAO.addUser(
          new User(None, user.name, user.email, user.gender, user.nationality, user.age)
        )
        Ok(views.html.index("User was added"))
      }
    )

  }

}

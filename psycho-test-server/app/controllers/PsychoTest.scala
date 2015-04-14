package controllers

import models.{TestDAO, UserDAO, User}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Call, Action}
import play.mvc.Controller
import play.api.mvc.Results._
import shared.Image

case class UserReq(email: String, name: String, nationality: String, gender: String, age: Int, testName: String)

object PsychoTest extends Controller {

  val userMapping =
    mapping(
      "email" -> email,
      "name" -> nonEmptyText(maxLength = 200),
      "nationality" -> nonEmptyText(maxLength = 200),
      "gender" -> nonEmptyText(maxLength = 200),
      "age" -> number(min = 0),
      "test" -> nonEmptyText
    )(UserReq.apply)(UserReq.unapply)

  val testForm: Form[UserReq] = Form(userMapping)

  def startTest() = Action { implicit req =>
    testForm.bindFromRequest.fold(
      errors =>
        BadRequest(views.html.tests(TestDAO.findAll, errors)),
      user => {
        val addedUser: User = new User(None, user.name, user.email, user.gender, user.nationality, user.age)
        //        UserDAO.addUser(
        //          addedUser
        //        )
        user.testName match {
          case "Kagan test" => {
            val kaganImages = new Array[Image](8)
            for (i <- 1 to 8) {
              kaganImages(i - 1) = new Image("kagan", 1, i)
            }
            val pattern = new Image("kagan", 1, 0)
            Ok(views.html.kaganTest(addedUser, pattern, kaganImages))
          }
          case _ => Ok(views.html.tests(TestDAO.findAll, PsychoTest.testForm))
        }
      }
    )

  }

  def finishTest(id: String) = Action {
    Ok(views.html.index(id + "|" + shared.SharedCode.getReport(id)))
  }
}

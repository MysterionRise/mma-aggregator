package controllers

import java.io.File

import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{DiscardingCookie, Call, Action}
import play.mvc.Controller
import play.api.mvc.Results._
import shared.{UltraRapidImage, Image}

import scala.collection.mutable.ArrayBuffer

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

  def readAllUltraRapidImages(): ArrayBuffer[UltraRapidImage] = {
    val res = new ArrayBuffer[UltraRapidImage]()
    for (i <- 1 to 6) {
      i match {
        case 1 => for (j <- 1 to 80) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 2 => for (j <- 1 to 122) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 3 => for (j <- 1 to 105) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 4 => for (j <- 1 to 95) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 5 => for (j <- 1 to 104) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
        case 6 => for (j <- 1 to 99) {
          res.append(new UltraRapidImage(String.valueOf(i), String.valueOf(i) + "/" + String.valueOf(j)))
        }
      }
    }
    res
  }

  def startTest() = Action { implicit req =>
    testForm.bindFromRequest.fold(
      errors =>
        BadRequest(views.html.tests(TestDAO.findAll, errors)),
      user => {
        val addedUser: User = new User(None, user.name, user.email, user.gender, user.nationality, user.age)
        //        val id = UserDAO.addUser(addedUser)
        //        addedUser.id = Some(id)
        user.testName match {
          case "Kagan test" => {
            val kaganImages = new Array[Image](8)
            for (i <- 1 to 8) {
              kaganImages(i - 1) = new Image("kagan", 1, i)
            }
            val pattern = new Image("kagan", 1, 0)
            Ok(views.html.kaganTest(addedUser, pattern, kaganImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case "Ultra rapid recognition" => {
            // TODO add more stuff to pass here
            Ok(views.html.ultraRapidTest(addedUser, readAllUltraRapidImages)).withNewSession.discardingCookies(DiscardingCookie("PLAY_SESSION", "/tests"))
          }
          case _ => Ok(views.html.tests(TestDAO.findAll, PsychoTest.testForm))
        }
      }
    )

  }

  def extractUserName(s: String): String = {
    return s.substring(s.indexOf("=") + 1, s.indexOf("|"))
  }

  def finishTest(report: String) = Action { implicit req =>
    val userID = extractUserName(report)
    ReportDAO.addReport(new Report(None, userID, report.substring(report.indexOf("=") + 1, report.length - 1)))
    Ok(views.html.index("You successfully finish testing! Go and check more new tests!"))
  }
}

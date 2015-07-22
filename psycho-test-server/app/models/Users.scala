package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Success, Failure}

case class User(var id: Option[Int], name: String, email: String, gender: String, nationality: String, age: Int, testId: Int)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def email = column[String]("email")

  def gender = column[String]("gender")

  def nationality = column[String]("nationality")

  def age = column[Int]("age")

  def testId = column[Int]("test_id")

  override def * = (id.?, name, email, gender, nationality, age, testId) <>(User.tupled, User.unapply _)
}

object UserDAO {
  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  lazy val db = dbConfig.db
  lazy val users = TableQuery[Users]

  def createSchema = {
    db.run(MTable.getTables).onComplete {
      case Success(value) => value.filter(table => table.name.name == users.baseTableRow.tableName).foreach(x => db.run(users.schema.create))
      case Failure(e) => e.printStackTrace
    }
  }

  def addUser(user: User): Int = {
    val action = (users returning users.map(_.id)) += user
    db.run(action).onComplete {
      case Success(value) => println(value)
      case Failure(e) => println(e.getStackTrace)
    }
    100
  }
}
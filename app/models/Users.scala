package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._

case class User(id: Int, name: String, email: String, gender: String, nationality: String, age: Int)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("user_id", O.PrimaryKey)

  def name = column[String]("name")

  def email = column[String]("email")

  def gender = column[String]("gender")

  def nationality = column[String]("nationality")

  def age = column[Int]("age")

  override def * = (id, name, email, gender, nationality, age) <>(User.tupled, User.unapply _)
}

object UserDAO {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def findAll = database.withSession { implicit db: Session =>
    val users = TableQuery[Users]
    users.list
  }
}
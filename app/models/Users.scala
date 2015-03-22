package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

case class User(id: Option[Int], name: String, email: String, gender: String, nationality: String, age: Int)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def email = column[String]("email")

  def gender = column[String]("gender")

  def nationality = column[String]("nationality")

  def age = column[Int]("age")

  override def * = (id.?, name, email, gender, nationality, age) <>(User.tupled, User.unapply _)
}

object UserDAO {
  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val users = TableQuery[Users]

  def createSchema = database.withSession { implicit db: Session =>
    if (!MTable.getTables.list.exists(_.name.name == users.baseTableRow.tableName)) {
      users.ddl.create
    }
  }

  def findAll = database.withSession { implicit db: Session =>
    users.list
  }

  def addUser(user: User) = database.withSession { implicit db: Session =>
    users.insert(user)
  }
}
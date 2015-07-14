package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

case class User(var id: Option[Int], name: String, email: String, gender: String, nationality: String, age: Int, testId: Int)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def email = column[String]("email")

  def gender = column[String]("gender")

  def nationality = column[String]("nationality")

  def age = column[Int]("age")

  def testId = column[Int]("testId")

  override def * = (id.?, name, email, gender, nationality, age, testId) <>(User.tupled, User.unapply _)
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

  def addUser(user: User): Int = database.withSession { implicit db: Session =>
    val userId =(users returning users.map(_.id)) += user
    return userId
  }
}
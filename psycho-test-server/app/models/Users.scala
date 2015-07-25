package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await
import scala.concurrent.duration._

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
  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(Play.current)
  val users = TableQuery[Users]

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(dbConfig.db.run(a), 5 seconds )

  def createSchema = {
    val result1 = result(MTable.getTables)
    result1.foreach(x => println(x))
    result1.filter(
      table => table.name.name == users.baseTableRow.tableName).foreach(x => result(users.schema.create))
  }

  def addUser(user: User): Int = {
    val action = (users returning users.map(_.id)) += user
    result[Int](action)
  }
}
package models

import java.net.URI

import play.api.Play
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
  val dbUri = new URI(Play.current.configuration.getString("slick.dbs.default.db.url").get)
  val username = dbUri.getUserInfo.split(":")(0)
  val password = dbUri.getUserInfo.split(":")(1)
  val dbUrl = s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"
  private lazy val db = Database.forURL(dbUrl, driver="org.postgresql.Driver", user = username, password = password)

  private val users = TableQuery[Users]

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(db.run(a), 5 seconds)

  def createSchema = {
    val not = result(MTable.getTables(users.baseTableRow.tableName))
    println(s"Users = ${not.size}")
    if (not.isEmpty) result(users.schema.create)
  }

  def addUser(user: User): Int = {
    val action = (users returning users.map(_.id)) += user
    result[Int](action)
  }
}
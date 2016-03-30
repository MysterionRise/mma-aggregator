package models

import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

case class User(var id: Option[Int], name: String, email: String, gender: String, nationality: String, age: Int,
                drivingLicense: String, drivingExperience: Int, pet: String, testId: Int)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def email = column[String]("email")

  def gender = column[String]("gender")

  def nationality = column[String]("nationality")

  def age = column[Int]("age")

  def testId = column[Int]("test_id")

  def drivingLicense = column[String]("driving-license")

  def drivingExperience = column[Int]("driving-experience")

  def pet = column[String]("pet")

  override def * = (id.?, name, email, gender, nationality, age, drivingLicense, drivingExperience, pet, testId) <>(User.tupled, User.unapply _)
}

object UserDAO extends BaseDAO {

  lazy val users = TableQuery[Users]

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
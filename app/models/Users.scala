package models

import scala.slick.driver.PostgresDriver.simple._

class Users(tag: Tag) extends Table[(Int, String, String, String, String, Int)](tag, "users") {
  def id = column[Int]("user_id", O.PrimaryKey)

  def name = column[String]("name")

  def email = column[String]("email")

  def gender = column[String]("gender")

  def nationality = column[String]("nationality")

  def age = column[Int]("age")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, email, gender, nationality, age)
}
//
//val users = TableQuery[Users]
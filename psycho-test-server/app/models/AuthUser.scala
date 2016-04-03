package models

import java.util.concurrent.TimeUnit

import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

case class AuthUser(id: Int, name: String, password: String)

class AuthUsers(tag: Tag) extends Table[AuthUser](tag, "auth_users") {
  def id = column[Int]("user_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def password = column[String]("password")

  override def * = (id, name, password) <>(AuthUser.tupled, AuthUser.unapply _)
}

object AuthUsersDAO extends BaseDAO {


  private val users = TableQuery[AuthUsers]

  def createSchema = {
    val not = result(MTable.getTables(users.baseTableRow.tableName))
    if (not.isEmpty) result(users.schema.create)
  }

  def md5Hash(text: String): String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map {
    "%02x".format(_)
  }.foldLeft("") {
    _ + _
  }

  /**
    * Authenticate a User.
    */
  def authenticate(name: String, password: String): Int = {
    val q1 = for (u <- users if u.name === name && u.password === md5Hash(password)) yield u
    val size = db.run(q1.size.result)
    Await.result(size, FiniteDuration.apply(10, TimeUnit.SECONDS))
  }
}
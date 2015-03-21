package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._

case class Test(id: Int, name: String, description: Option[String])

class Tests(tag: Tag) extends Table[Test](tag, "tests") {
  def id = column[Int]("test_id", O.PrimaryKey)

  def name = column[String]("name")

  def description = column[String]("description")

  override def * = (id, name, description.?) <> (Test.tupled, Test.unapply _)
}

object TestDAO {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def findAll = database.withSession { implicit db: Session =>
    val tests = TableQuery[Tests]
    tests.list
  }
}
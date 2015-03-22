package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

case class Test(id: Int, name: String, description: Option[String])

class Tests(tag: Tag) extends Table[Test](tag, "tests") {
  def id = column[Int]("test_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[String]("description", O.Nullable)

  override def * = (id, name, description.?) <> (Test.tupled, Test.unapply _)
}

object TestDAO {
  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val tests = TableQuery[Tests]

  def createSchema = database.withSession { implicit db: Session =>
    if (!MTable.getTables.list.exists(_.name.name == tests.baseTableRow.tableName)) {
      tests.ddl.create
    }
  }

  def findAll = database.withSession { implicit db: Session =>
    tests.list
  }
}
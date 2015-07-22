package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class Test(id: Int, name: String, description: Option[String])

class Tests(tag: Tag) extends Table[Test](tag, "tests") {
  def id = column[Int]("test_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[Option[String]]("description")

  override def * = (id, name, description) <>(Test.tupled, Test.unapply _)
}

object TestDAO {

  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(Play.current)
  lazy val db = dbConfig.db
  lazy val tests = TableQuery[Tests]

  def createSchema = {
    db.run(MTable.getTables).onComplete {
      case Success(value) => value.filter(table => table.name.name == tests.baseTableRow.tableName).foreach(x => db.run(tests.schema.create))
      case Failure(e) => e.printStackTrace
    }
  }

  def findAll: List[Test] = List()
}
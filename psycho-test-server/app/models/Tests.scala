package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration._

case class Test(id: Int, name: String, description: Option[String])

class Tests(tag: Tag) extends Table[Test](tag, "tests") {
  def id = column[Int]("test_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[Option[String]]("description")

  override def * = (id, name, description) <>(Test.tupled, Test.unapply _)
}

object TestDAO {
  private lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(Play.current)
  lazy val tests = TableQuery[Tests]
  lazy val db = dbConfig.db

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(dbConfig.db.run(a), 5 seconds)

  def createSchema = {
    val not = result(MTable.getTables(tests.baseTableRow.tableName))
    println(s"Tests = ${not.size}")
    if (not.isEmpty) result(tests.schema.create)
  }

  def findAll: List[Test] = {
    val buffer = new ArrayBuffer[Test]()
    result(tests.result).foreach(buffer.append(_))
    buffer.toList
  }
}
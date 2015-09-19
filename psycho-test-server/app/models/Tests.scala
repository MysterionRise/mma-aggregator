package models

import java.net.URI

import play.api.Play
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.slick.driver

case class Test(id: Int, name: String, description: Option[String])

class Tests(tag: Tag) extends Table[Test](tag, "tests") {
  def id = column[Int]("test_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def description = column[Option[String]]("description")

  override def * = (id, name, description) <>(Test.tupled, Test.unapply _)
}

object TestDAO extends BaseDAO {

  lazy val tests = TableQuery[Tests]

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
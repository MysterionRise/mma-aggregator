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

object TestDAO {
  val dbUri = new URI(Play.current.configuration.getString("slick.dbs.default.db.url").get)
  val username = dbUri.getUserInfo.split(":")(0)
  val password = dbUri.getUserInfo.split(":")(1)
  val dbUrl = s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"
  lazy val db = Database.forURL(dbUrl, driver="org.postgresql.Driver", user = username, password = password)

  lazy val tests = TableQuery[Tests]

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(db.run(a), 5 seconds)

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
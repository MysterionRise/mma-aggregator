package models

import java.net.URI

import play.api.Play
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await
import scala.concurrent.duration._

case class Report(id: Option[Int], userID: Int, report: String)

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
  def id = column[Int]("report_id", O.PrimaryKey, O.AutoInc)

  def name = column[Int]("user_id")

  def report = column[String]("report", O.SqlType("VARCHAR(10000)"))

  override def * = (id.?, name, report) <>(Report.tupled, Report.unapply _)
}

object ReportDAO {
  val dbUri = new URI(Play.current.configuration.getString("slick.dbs.default.db.url").get)
  val username = dbUri.getUserInfo.split(":")(0)
  val password = dbUri.getUserInfo.split(":")(1)
  val dbUrl = s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"
  private lazy val db = Database.forURL(dbUrl, driver="org.postgresql.Driver", user = username, password = password)

  private val reports = TableQuery[Reports]

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(db.run(a), 5 seconds)

  def createSchema = {
    val not = result(MTable.getTables(reports.baseTableRow.tableName))
    println(s"Reports = ${not.size}")
    if (not.isEmpty) result(reports.schema.create)
  }

  def addReport(report: Report) = db.run(DBIO.seq(reports += report))

}
package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
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
  private lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(Play.current)
  private val reports = TableQuery[Reports]

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(dbConfig.db.run(a), 5 seconds)

  def createSchema = {
    val not = result(MTable.getTables(reports.baseTableRow.tableName))
    println(s"Reports = ${not.size}")
    if (not.isEmpty) result(reports.schema.create)
  }

  def addReport(report: Report) = dbConfig.db.run(DBIO.seq(reports += report))

}
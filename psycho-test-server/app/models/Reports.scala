package models

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Success, Failure}

case class Report(id: Option[Int], userID: Int, report: String)

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
  def id = column[Int]("report_id", O.PrimaryKey, O.AutoInc)

  def name = column[Int]("user_id")

  def report = column[String]("report", O.SqlType("VARCHAR(10000)"))

  override def * = (id.?, name, report) <>(Report.tupled, Report.unapply _)
}

object ReportDAO {
  lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(Play.current)
  lazy val db = dbConfig.db
  val reports = TableQuery[Reports]

  def createSchema = {
    db.run(MTable.getTables).onComplete {
      case Success(value) =>
        value.filter(table => table.name.name == reports.baseTableRow.tableName).foreach(x => db.run(reports.schema.create))
      case Failure(e) => e.printStackTrace
    }
  }

  def addReport(report: Report) = db.run(DBIO.seq(reports += report))

}
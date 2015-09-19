package models

import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable

case class Report(id: Option[Int], userID: Int, report: String)

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
  def id = column[Int]("report_id", O.PrimaryKey, O.AutoInc)

  def name = column[Int]("user_id")

  def report = column[String]("report", O.SqlType("VARCHAR(10000)"))

  override def * = (id.?, name, report) <>(Report.tupled, Report.unapply _)
}

object ReportDAO extends BaseDAO {

  private val reports = TableQuery[Reports]

  def createSchema = {
    val not = result(MTable.getTables(reports.baseTableRow.tableName))
    println(s"Reports = ${not.size}")
    if (not.isEmpty) result(reports.schema.create)
  }

  def addReport(report: Report) = db.run(DBIO.seq(reports += report))

}
package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

case class Report(id: Option[Int], userID: Int, report: String)

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
  def id = column[Int]("report_id", O.PrimaryKey, O.AutoInc)

  def name = column[Int]("user_id")

  def report = column[String]("report", O.DBType("VARCHAR(10000)"))

  override def * = (id.?, name, report) <> (Report.tupled, Report.unapply _)
}

object ReportDAO {
  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val reports = TableQuery[Reports]

  def createSchema = database.withSession { implicit db: Session =>
    if (!MTable.getTables.list.exists(_.name.name == reports.baseTableRow.tableName)) {
      reports.ddl.create
    }
  }

  def findAll = database.withSession { implicit db: Session =>
    reports.list
  }

  def addReport(report: Report) = database.withSession { implicit db: Session =>
    reports.insert(report)
  }
}
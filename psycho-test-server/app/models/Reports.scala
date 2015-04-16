package models


import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

// TODO [PSQLException: ERROR: value too long for type character varying(254)]
case class Report(id: Option[Int], userID: String, report: String)

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
  def id = column[Int]("report_id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("user_id")

  def report = column[String]("report")

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
package models

import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.PostgresDriver.simple._

sealed trait Frequency

case object Daily extends Frequency

case object Weekly extends Frequency

case object Monthly extends Frequency

case class Subscription(id: Int, email: String, frequency: Frequency)

class Subscriptions(tag: Tag) extends Table[Subscription](tag, "subscriptions") {

  implicit val resourceTypeTypeMapper = MappedColumnType.base[Frequency, Int](
  { freq => if (freq == Daily) 0 else if (freq == Weekly) 1 else 2},
  { i => if (i == 0) Daily else if (i == 1) Weekly else Monthly}
  )

  def id = column[Int]("sub_id", O.PrimaryKey)

  def email = column[String]("email")

  def frequency = column[Frequency]("frequency")

  //  // Every table needs a * projection with the same type as the table's type parameter
  //  def * = id.? ~ email <> (Subscription.apply _, Subscription.unapply _)
  override def * = (id, email, frequency) <>(Subscription.tupled, Subscription.unapply _)
}

object SubscriptionDAO {

  lazy val database = Database.forDataSource(DB.getDataSource())

  def findAll = database.withSession { implicit db:Session =>
    val subscriptions = TableQuery[Subscriptions]
    subscriptions.list
  }
}


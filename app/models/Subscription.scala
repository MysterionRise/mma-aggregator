package models

import play.api.Play.current
import play.api.mvc._
import play.api.db._
import anorm.{Row, SQL}

sealed trait Frequency

case object Daily extends Frequency

case object Weekly extends Frequency

case object Monthly extends Frequency

case class Subscription(
                         email: String,
                         frequency: Frequency
                         )

object Subscription {

  def getAll: List[Subscription] = {

    val conn = DB.getConnection()
    val subscriptions = SQL("Select email,frequency from subscriptions")().collect {
      case Row(name: String, "daily") => new Subscription(name, Daily)
      case Row(name: String, "weekly") => new Subscription(name, Weekly)
      case Row(name: String, "monthly") => new Subscription(name, Monthly)
    }
    return subscriptions.toList
  }
}


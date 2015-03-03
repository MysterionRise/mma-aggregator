package models

import anorm._
import play.api.db.DB

sealed trait Frequency

case object Daily extends Frequency

case object Weekly extends Frequency

case object Monthly extends Frequency

case class Subscription(
                         email: String,
                         frequency: Frequency
                         )

object Subscription {

  val connection = DB.getConnection()

  //  def getAll = List(Subscription("test@test.example.com", Daily), Subscription("test@test.gmail.com", Monthly))

  def getAll: List[Subscription] = {

    val l: List[Subscription] = List()

    try {
      val stmt = connection.createStatement
      val rs = stmt.executeQuery("SELECT test@test.com as email ")
      while (rs.next()) {
        val s = new Subscription(rs.getString("email"), Daily)
        l :+ s
      }
      return l
    } finally {
      connection.close()
    }
  }
}


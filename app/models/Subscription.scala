package models

import play.api.Play.current
import play.api.mvc._
import play.api.db._

sealed trait Frequency

case object Daily extends Frequency

case object Weekly extends Frequency

case object Monthly extends Frequency

case class Subscription(
                         email: String,
                         frequency: Frequency
                         )

object Subscription {

  //  def getAll = List(Subscription("test@test.example.com", Daily), Subscription("test@test.gmail.com", Monthly))

  def getAll: List[Subscription] = {

    val l: List[Subscription] = List()

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT 9 as testkey ")
      while (rs.next()) {
        val s = new Subscription(rs.getString("testkey"), Daily)
        l :+ s
      }
    } finally {
      conn.close()
    }
    //    var conn: Connection = null
    //    try {
    //      conn = DB.getConnection("mydb")
    //      val stmt = conn.createStatement
    //      val rs = stmt.executeQuery("SELECT test@test.com as email ")
    //      while (rs.next()) {
    //        val s = new Subscription(rs.getString("email"), Daily)
    //        l :+ s
    //      }
    //    } finally {
    //      if (conn != null) {
    //        conn.close()
    //      }
    //    }
    return l
  }
}


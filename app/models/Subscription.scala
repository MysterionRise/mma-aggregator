package models

import java.sql.Connection
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

    val ds = DB.getDataSource()
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


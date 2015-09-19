package models

import java.net.URI

import play.api.Play
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * for the purpose of extracting same code
 */
class BaseDAO {

  val dbUri = new URI(Play.current.configuration.getString("slick.dbs.default.db.url").get)
  val username = dbUri.getUserInfo.split(":")(0)
  val password = dbUri.getUserInfo.split(":")(1)
  val dbUrl = s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}"

  protected lazy val db = Database.forURL(dbUrl, driver="org.postgresql.Driver", user = username, password = password)

  def getDB() = db

  def result[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result(db.run(a), 5 seconds)

}

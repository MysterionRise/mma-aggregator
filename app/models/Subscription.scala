package models

import play.api.Play
import play.api.Play.current
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.novus.salat.global._

sealed trait Frequency

case object Daily extends Frequency

case object Weekly extends Frequency

case object Monthly extends Frequency

case class Subscription(
                         id: ObjectId,
                         email: String,
                         frequency: Frequency
                         )

class SubscriptionDao extends SalatDAO[Subscription, ObjectId](collection = MongoConnection()(Play.current.configuration.getString("mongo.database").get)("subscriptions"))

object Subscription extends ModelCompanion[Subscription, ObjectId] {

  val dao = new SubscriptionDao

//  def findOneByUsername(username: String): Option[Subscription] = dao.findOne(MongoDBObject("username" -> username))
//
//  def findByCountry(country: String) = dao.find(MongoDBObject("address.country" -> country))

  def getAll = findAll().toList
}


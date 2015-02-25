package models

case class Person(name: String)

object Person {
  def getAll = List(Person("Bob"), Person("Dave"), Person("Nick"), Person("Bastian"))
}

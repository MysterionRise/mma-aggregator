package models

/**
 * TODO use it as future template
 */
case class Person(name: String)

object Person {
  def getAll = List(Person("Bob"), Person("Dave"), Person("Nick"), Person("Bastian"))
}

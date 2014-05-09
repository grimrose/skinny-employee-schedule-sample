package model

object Gender {
  def all() = List(NotKnown, Male, Female, NotApplicable)

  case object NotKnown extends Gender(0)
  case object Male extends Gender(1)
  case object Female extends Gender(2)
  case object NotApplicable extends Gender(9)

}

sealed abstract class Gender(val code:Int)

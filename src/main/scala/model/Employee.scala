package model

import skinny.orm._, feature._
import scalikejdbc._, SQLInterpolation._
import org.joda.time._

// If your model has +23 fields, switch this to normal class and mixin scalikejdbc.EntityEquality.
case class Employee(
    id: Long,
    firstName: String,
    middleName: String,
    lastName: String,
    gender: Int,
    startedEmployment: Option[LocalDate] = None,
    leftEmployment: Option[LocalDate] = None,
    otherDetail: Option[String] = None,
    schedules: Seq[Schedule] = Nil,
    createdAt: DateTime,
    updatedAt: DateTime) {

  def fullName = Seq(firstName, middleName, lastName).mkString(" ")
}

object Employee extends SkinnyCRUDMapper[Employee] with TimestampsFeature[Employee] {
  override lazy val tableName = "employees"
  override lazy val defaultAlias = createAlias("e")

  override def extract(rs: WrappedResultSet, rn: ResultName[Employee]): Employee = new Employee(
    id = rs.get(rn.id),
    firstName = rs.get(rn.firstName),
    middleName = rs.get(rn.middleName),
    lastName = rs.get(rn.lastName),
    gender = rs.get(rn.gender),
    startedEmployment = rs.get(rn.startedEmployment),
    leftEmployment = rs.get(rn.leftEmployment),
    otherDetail = rs.get(rn.otherDetail),
    createdAt = rs.get(rn.createdAt),
    updatedAt = rs.get(rn.updatedAt)
  )

  val schedulesRef = hasManyThrough[Schedule](
    through = EmployeeSchedule,
    many = Schedule,
    merge = (employee, schedules) => employee.copy(schedules = schedules)
  )

}

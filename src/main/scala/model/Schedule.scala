package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

// If your model has +23 fields, switch this to normal class and mixin scalikejdbc.EntityEquality.
case class Schedule(
  id: Long,
  plannedScheduleId: Long,
  start: DateTime,
  end: Option[DateTime] = None,
  otherDetails: Option[String] = None,
  plannedSchedule: Option[PlannedSchedule] = None,
  employees: Seq[Employee] = Nil,
  createdAt: DateTime,
  updatedAt: DateTime)

object Schedule extends SkinnyCRUDMapper[Schedule] with TimestampsFeature[Schedule] {
  override lazy val tableName = "schedules"
  override lazy val defaultAlias = createAlias("s")

  override def extract(rs: WrappedResultSet, rn: scalikejdbc.ResultName[Schedule]): Schedule = new Schedule(
    id = rs.get(rn.id),
    plannedScheduleId = rs.get(rn.plannedScheduleId),
    start = rs.get(rn.start),
    end = rs.get(rn.end),
    otherDetails = rs.get(rn.otherDetails),
    createdAt = rs.get(rn.createdAt),
    updatedAt = rs.get(rn.updatedAt)
  )

  belongsTo[PlannedSchedule](
    right = PlannedSchedule,
    merge = (schedule, plannedSchedule) => schedule.copy(plannedSchedule = plannedSchedule)
  ).byDefault

  val employeesRef = hasManyThrough[Employee](
    through = EmployeeSchedule,
    many = Employee,
    merge = (s, employees) => s.copy(employees = employees)
  )

}

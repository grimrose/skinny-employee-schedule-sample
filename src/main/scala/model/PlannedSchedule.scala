package model

import org.joda.time._
import scalikejdbc._
import skinny.orm._
import skinny.orm.feature._

// If your model has +23 fields, switch this to normal class and mixin scalikejdbc.EntityEquality.
case class PlannedSchedule(
  id: Long,
  name: String,
  description: Option[String] = None,
  otherDetails: Option[String] = None,
  createdAt: DateTime,
  updatedAt: DateTime)

object PlannedSchedule extends SkinnyCRUDMapper[PlannedSchedule] with TimestampsFeature[PlannedSchedule] {
  override lazy val tableName = "planned_schedules"
  override lazy val defaultAlias = createAlias("ps")

  override def extract(rs: WrappedResultSet, rn: scalikejdbc.ResultName[PlannedSchedule]): PlannedSchedule = new PlannedSchedule(
    id = rs.get(rn.id),
    name = rs.get(rn.name),
    description = rs.get(rn.description),
    otherDetails = rs.get(rn.otherDetails),
    createdAt = rs.get(rn.createdAt),
    updatedAt = rs.get(rn.updatedAt)
  )
}

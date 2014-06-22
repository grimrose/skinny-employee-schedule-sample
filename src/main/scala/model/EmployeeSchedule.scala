package model

import skinny.orm.{ Alias, SkinnyJoinTable }
import scalikejdbc._
import SQLInterpolation._
import skinny.{ ParamType, PermittedStrongParameters }

case class EmployeeSchedule(
  employeeId: Long,
  scheduleId: Long,
  employee: Option[Employee] = None,
  schedule: Option[Schedule] = None)

object EmployeeSchedule extends SkinnyJoinTable[EmployeeSchedule] {

  override val tableName = "employees_schedules"
  override val defaultAlias = createAlias("es")

  override def defaultOrdering = defaultAlias.field("employeeId")

  override def extract(rs: WrappedResultSet, rn: ResultName[EmployeeSchedule]): EmployeeSchedule = new EmployeeSchedule(
    employeeId = rs.get(rn.employeeId),
    scheduleId = rs.get(rn.scheduleId)
  )

  belongsTo[Employee](
    right = Employee,
    merge = (employeeSchedule, employee) => employeeSchedule.copy(employee = employee)
  ).byDefault
  belongsTo[Schedule](
    right = Schedule,
    merge = (employeeSchedule, schedule) => employeeSchedule.copy(schedule = schedule)
  ).byDefault

  def createWithStrongParameters(strongParameters: PermittedStrongParameters)(implicit s: DBSession = autoSession): Map[String, Long] = {
    createWithPermittedAttributes(strongParameters)
    strongParameters.params.filterKeys(_.contains("_id")).map {
      case (name, (value, paramType)) => column.field(name) -> ParamType.Long.unapply(value)
    }.map(kv => kv._1.value -> kv._2.fold(-1L)((x) => x.toString.toLong))
  }

  def deleteByEmployeeIdAndScheduleId(employeeId: Long, scheduleId: Long)(implicit session: DBSession = autoSession): Int = {
    deleteBy(sqls.eq(EmployeeSchedule.column.employeeId, employeeId).and.eq(EmployeeSchedule.column.scheduleId, scheduleId))
  }

  def findByEmployeeIdAndScheduleId(employeeId: Long, scheduleId: Long)(implicit session: DBSession = autoSession): Option[EmployeeSchedule] = {
    val es = defaultAlias
    findBy(sqls.eq(es.employeeId, employeeId).and.eq(es.scheduleId, scheduleId))
  }

  def createScheduleIdAndEmployeeIds(scheduleId: Long, employeeIds: Seq[Long])(implicit session: DBSession = autoSession): Seq[Int] = {
    val es = column
    val batchParams = employeeIds.map(employeeId => Seq(employeeId, scheduleId))
    insert.into(EmployeeSchedule).namedValues(es.employeeId -> sqls.?, es.scheduleId -> sqls.?).toSQL.batch(batchParams: _*).apply()
  }

}

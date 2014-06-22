package model

import org.scalatest.fixture
import org.scalatest.matchers.ShouldMatchers
import skinny.{ StrongParameters, ParamType, PermittedStrongParameters, DBSettings }
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.DBSession
import scalikejdbc.SQLInterpolation._
import skinny.test.FactoryGirl

class EmployeeScheduleSpec extends fixture.FunSpec with ShouldMatchers with DBSettings with AutoRollback {
  override def fixture(implicit session: DBSession): Unit = {
    delete.from(EmployeeSchedule).toSQL.execute().apply()
    delete.from(Employee).toSQL.execute().apply()
    delete.from(Schedule).toSQL.execute().apply()
    delete.from(PlannedSchedule).toSQL.execute().apply()
  }

  describe("EmployeeSchedule") {
    it("should insert") { implicit session =>
      val employee: Employee = FactoryGirl.apply(Employee).create()
      val plannedSchedule: PlannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      val schedule: Schedule = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create()
      EmployeeSchedule.createWithAttributes('employeeId -> employee.id, 'scheduleId -> schedule.id)

      Employee.joins(Employee.schedulesRef).findById(employee.id).map(_.schedules).get.map(_.id) should equal(Seq(schedule.id))
      Schedule.joins(Schedule.employeesRef).findById(schedule.id).map(_.employees).get.map(_.id) should be(Seq(employee.id))
    }
    it("should insert by schedules") { implicit session =>
      val employee: Employee = FactoryGirl.apply(Employee).create()
      val plannedSchedule: PlannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      val schedule_1 = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create().id
      val schedule_2 = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create().id
      EmployeeSchedule.createWithAttributes('employeeId -> employee.id, 'scheduleId -> schedule_1)
      EmployeeSchedule.createWithAttributes('employeeId -> employee.id, 'scheduleId -> schedule_2)

      Employee.joins(Employee.schedulesRef).findById(employee.id).map(_.schedules).map(_.map(_.id)) should be(Some(Seq(schedule_1, schedule_2)))
    }
    it("should delete by employee id and schedule id") { implicit session =>
      val employee: Employee = FactoryGirl.apply(Employee).create()
      val plannedSchedule: PlannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      val schedule: Schedule = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create()
      EmployeeSchedule.createWithAttributes('employeeId -> employee.id, 'scheduleId -> schedule.id)

      EmployeeSchedule.deleteByEmployeeIdAndScheduleId(employee.id, schedule.id) should be(1)
      EmployeeSchedule.count() should be(0)
    }
    it("should find by employee id and schedule id") { implicit session =>
      val employee: Employee = FactoryGirl.apply(Employee).create()
      val plannedSchedule: PlannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      val schedule_1 = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create().id
      val schedule_2 = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create().id
      EmployeeSchedule.createWithAttributes('employeeId -> employee.id, 'scheduleId -> schedule_1)

      EmployeeSchedule.findByEmployeeIdAndScheduleId(employee.id, schedule_1).isDefined should equal(true)
      EmployeeSchedule.findByEmployeeIdAndScheduleId(employee.id, schedule_2).isDefined should equal(false)
    }
    it("should insert with strong parameter") { implicit session =>
      val employee_id = FactoryGirl.apply(Employee).create().id
      val plannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      val schedule_id = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create().id

      val params = Map("employee_id" -> employee_id, "schedule_id" -> schedule_id)
      val strongParameters: PermittedStrongParameters = StrongParameters(params).permit(Seq("employee_id" -> ParamType.Long, "schedule_id" -> ParamType.Long): _*)
      val parameters = EmployeeSchedule.createWithStrongParameters(strongParameters)

      parameters.getOrElse("employee_id", -1L) should be(employee_id)
      parameters.getOrElse("schedule_id", -1L) should be(schedule_id)
      EmployeeSchedule.findByEmployeeIdAndScheduleId(employee_id, schedule_id).isDefined should equal(true)
    }
    it("should insert with schedule and employees parameter") { implicit session =>
      val plannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      val scheduleId = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> plannedSchedule.id).create().id
      val employeeId1 = FactoryGirl.apply(Employee).create().id
      val employeeId2 = FactoryGirl.apply(Employee).create().id

      EmployeeSchedule.createScheduleIdAndEmployeeIds(scheduleId, Seq(employeeId1, employeeId2)) should equal(Seq(1, 1))
    }
  }

}

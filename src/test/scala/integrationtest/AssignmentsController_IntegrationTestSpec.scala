package integrationtest

import _root_.controller._
import org.scalatra.test.scalatest._
import skinny.test.{ FactoryGirl, SkinnyTestSupport }
import skinny.DBSettings
import model.{ EmployeeSchedule, Schedule, PlannedSchedule, Employee }

class AssignmentsController_IntegrationTestSpec extends ScalatraFlatSpec with SkinnyTestSupport with DBSettings {
  addFilter(Controllers.assignments, "/*")

  def newEmployee = FactoryGirl(Employee).create()

  def newSchedule = {
    val id = FactoryGirl(PlannedSchedule).create().id
    FactoryGirl(Schedule).withAttributes('plannedScheduleId -> id).create()
  }

  def newEmployeeSchedule = {
    val employee = newEmployee
    val schedule = newSchedule
    EmployeeSchedule.deleteAll()
    EmployeeSchedule.createWithAttributes('employeeId -> employee.id, 'scheduleId -> schedule.id)

    EmployeeSchedule.findByEmployeeIdAndScheduleId(employee.id, schedule.id).get
  }

  ignore should "show index page" in {

  }

  ignore should "show employees" in {

  }

  ignore should "show a employee schedules" in {

  }

  it should "show new entry form" in {
    val employee: Employee = newEmployee
    get(s"/assignments/employees/${employee.id}/schedules/new") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "delete a employee schedule" in {
    val employeeSchedule = newEmployeeSchedule
    delete(s"/assignments/employees/${employeeSchedule.employeeId}/schedules/${employeeSchedule.scheduleId}") {
      logBodyUnless(403)
      status should equal(403)
    }
    withSession("csrf-token" -> "valid_token") {
      val employeeSchedule1 = newEmployeeSchedule
      delete(s"/assignments/employees/${employeeSchedule1.employeeId}/schedules/${employeeSchedule1.scheduleId}?csrf-token=valid_token") {
        logBodyUnless(200)
        status should equal(200)
      }
    }
  }

}

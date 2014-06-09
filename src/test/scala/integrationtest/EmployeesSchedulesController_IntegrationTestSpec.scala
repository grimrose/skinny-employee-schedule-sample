package integrationtest

import org.scalatra.test.scalatest._
import skinny._
import skinny.test._
import _root_.controller.Controllers
import model._
import skinny.test.FactoryGirl

class EmployeesSchedulesController_IntegrationTestSpec extends ScalatraFlatSpec with SkinnyTestSupport with DBSettings {
  addFilter(Controllers.employeesSchedules, "/*")

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

  it should "show employees schedules" in {
    get("/employees_schedules") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/employees_schedules/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/employees_schedules.json") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/employees_schedules.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show a employee schedule in detail" in {
    val employeeSchedule = newEmployeeSchedule
    get(s"/employees_schedules/employees/${employeeSchedule.employeeId}/schedules/${employeeSchedule.scheduleId}") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/employees_schedules/employees/${employeeSchedule.employeeId}/schedules/${employeeSchedule.scheduleId}.json") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/employees_schedules/employees/${employeeSchedule.employeeId}/schedules/${employeeSchedule.scheduleId}.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "create a employee schedule" in {
    val employee = newEmployee
    val schedule = newSchedule
    post(s"/employees_schedules",
      "employee_id" -> employee.id.toString,
      "schedule_id" -> schedule.id.toString) {
        logBodyUnless(403)
        status should equal(403)
      }

    withSession("csrf-token" -> "valid_token") {
      val employee = newEmployee
      val schedule = newSchedule
      post(s"/employees_schedules",
        "employee_id" -> employee.id.toString,
        "schedule_id" -> schedule.id.toString,
        "csrf-token" -> "valid_token") {
          logBodyUnless(302)
          status should equal(302)
          EmployeeSchedule.findByEmployeeIdAndScheduleId(employee.id, schedule.id).isDefined should equal(true)
        }
    }
  }

}

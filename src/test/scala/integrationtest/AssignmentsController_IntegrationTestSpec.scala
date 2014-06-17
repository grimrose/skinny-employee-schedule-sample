package integrationtest

import _root_.controller._
import org.scalatra.test.scalatest._
import skinny.test.{FactoryGirl, SkinnyTestSupport}
import skinny.DBSettings
import model.{EmployeeSchedule, Schedule, PlannedSchedule, Employee}

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

  it should "show index page" in {
    get(s"/assignments") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/?") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show employees" in {
    get(s"/assignments/employees") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/employees/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/employees/?") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show schedules" in {
    get(s"/assignments/schedules") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/schedules/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/schedules/?") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show a employee schedules" in {
    val employee = newEmployee
    get(s"/assignments/employees/${employee.id}/schedules") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/employees/${employee.id}/schedules/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/assignments/employees/${employee.id}/schedules/?") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show employee new entry form" in {
    val employee = newEmployee
    get(s"/assignments/employees/${employee.id}/schedules/new") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show schedule new entry form" in {
    val schedule = newSchedule
    get(s"/assignments/schedules/${schedule.id}/employees/new") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  ignore should "assign schedule to employees" in {
    val employee1 = newEmployee
    val employee2 = newEmployee
    val schedule = newSchedule
    post(s"/assignments/schedules/${schedule.id}",
      "employee_id" -> employee1.id.toString,
      "employee_id" -> employee2.id.toString) {
      logBodyUnless(403)
      status should equal(403)
    }

    withSession("csrf-token" -> "valid_token") {
      val employee1 = newEmployee
      val employee2 = newEmployee
      val schedule = newSchedule
      post(s"/assignments/schedules/${schedule.id}",
        "employee_id" -> employee1.id.toString,
        "employee_id" -> employee2.id.toString,
        "csrf-token" -> "valid_token") {
        logBodyUnless(302)
        status should equal(302)
      }
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

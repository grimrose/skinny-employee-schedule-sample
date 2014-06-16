package controller

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import skinny._
import skinny.test._
import org.joda.time._
import model._
import scalikejdbc.SQLInterpolation._
import skinny.test.FactoryGirl
import scala.Some
import scalikejdbc.DBSession

// NOTICE before/after filters won't be executed by default
class EmployeesSchedulesControllerSpec extends FunSpec with ShouldMatchers with DBSettings {

  def createMockController = new EmployeesSchedulesController with MockController

  def newEmployee = FactoryGirl(Employee).create()
  def newSchedule = {
    val plannedScheduleId = FactoryGirl(PlannedSchedule).create().id
    FactoryGirl(Schedule).withAttributes('plannedScheduleId -> plannedScheduleId).create()
  }

  def newEmployeeSchedule = {
    val employeeId = FactoryGirl(Employee).create().id
    val scheduleId = newSchedule.id

    EmployeeSchedule.deleteAll()
    EmployeeSchedule.createWithAttributes('employeeId -> employeeId, 'scheduleId -> scheduleId)

    EmployeeSchedule.findByEmployeeIdAndScheduleId(employeeId, scheduleId).get
  }

  describe("EmployeesSchedulesController") {

    describe("shows employees schedules") {
      it("shows HTML response") {
        val controller = createMockController
        controller.showResources
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/employeesSchedules/index"))
        controller.contentType should equal("text/html; charset=utf-8")
      }
      it("shows JSON response") {
        implicit val format = Format.JSON
        val controller = createMockController
        controller.showResources
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/employeesSchedules/index"))
        controller.contentType should equal("application/json; charset=utf-8")
      }
    }

    describe("shows a employee schedule") {
      it("shows HTML response") {
        val employeeSchedule = newEmployeeSchedule
        val controller = createMockController
        controller.showResource(employeeSchedule.employeeId, employeeSchedule.scheduleId)
        controller.status should equal(200)
        controller.requestScope[EmployeeSchedule]("item") should equal(Some(employeeSchedule))
        controller.renderCall.map(_.path) should equal(Some("/employeesSchedules/show"))
      }
    }

    describe("creates a employee schedule") {
      it("succeeds with valid parameters") {
        val employee = newEmployee
        val schedule = newSchedule
        val controller = createMockController
        controller.prepareParams(
          "employee_id" -> employee.id.toString,
          "schedule_id" -> schedule.id.toString)
        controller.createResource
        controller.status should equal(200)
      }

      it("fails with invalid parameters") {
        val controller = createMockController
        controller.prepareParams() // no parameters
        controller.createResource
        controller.status should equal(400)
        controller.errorMessages.size should be > (0)
      }
    }

  }

}

package controller

import _root_.controller._
import _root_.model._
import org.scalatra.test.scalatest._
import skinny.test._
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import skinny.DBSettings

class AssignmentsControllerSpec extends FunSpec with ShouldMatchers with DBSettings {

  def createMockController = new AssignmentsController with MockController

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

  describe("AssignmentsController") {

    describe("show index page") {
      it("shows HTML response") {
        val controller = createMockController
        controller.index
        controller.contentType should equal("text/html; charset=utf-8")
        controller.renderCall.map(_.path) should equal(Some("/assignments/index"))
      }
    }

    describe("employees pages") {

      describe("shows employees") {
        it("shows HTML response") {
          val controller = createMockController
          controller.employeeAction
          controller.contentType should equal("text/html; charset=utf-8")
          controller.renderCall.map(_.path) should equal(Some("/assignments/employees/index"))
        }
      }

      describe("shows a employee schedules") {
        it("shows HTML response") {
          val employee = newEmployee
          val controller = createMockController
          controller.showEmployeeResource(employee.id)
          controller.status should equal(200)
          controller.requestScope[Employee]("employee") should equal(Some(employee))
          controller.renderCall.map(_.path) should equal(Some("/assignments/employees/show"))
        }
      }

      describe("shows new resource input form") {
        it("shows HTML response") {
          val employee = newEmployee
          val controller = createMockController
          controller.newEmployeeResource(employee.id)
          controller.status should equal(200)
          controller.renderCall.map(_.path) should equal(Some("/assignments/employees/new"))
        }
      }

      it("destroys a employee schedule") {
        val employeeSchedule = newEmployeeSchedule
        val controller = createMockController
        controller.destroyResource(employeeSchedule.employeeId, employeeSchedule.scheduleId)
        controller.status should equal(200)
      }
    }
  }

}
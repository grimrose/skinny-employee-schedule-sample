package controller

import org.scalatest.{Matchers, FunSpec}
import skinny._
import skinny.test._
import org.joda.time._
import model._

// NOTICE before/after filters won't be executed by default
class EmployeesControllerSpec extends FunSpec with Matchers with DBSettings {

  def createMockController = new EmployeesController with MockController
  def newEmployee = FactoryGirl(Employee).create()

  describe("EmployeesController") {

    describe("shows employees") {
      it("shows HTML response") {
        val controller = createMockController
        controller.showResources()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/employees/index"))
        controller.contentType should equal("text/html; charset=utf-8")
      }

      it("shows JSON response") {
        implicit val format = Format.JSON
        val controller = createMockController
        controller.showResources()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/employees/index"))
        controller.contentType should equal("application/json; charset=utf-8")
      }
    }

    describe("shows a employee") {
      it("shows HTML response") {
        val employee = newEmployee
        val controller = createMockController
        controller.showResource(employee.id)
        controller.status should equal(200)
        controller.requestScope[Employee]("item") should equal(Some(employee))
        controller.renderCall.map(_.path) should equal(Some("/employees/show"))
      }
    }

    describe("shows new resource input form") {
      it("shows HTML response") {
        val controller = createMockController
        controller.newResource()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/employees/new"))
      }
    }

    describe("creates a employee") {
      it("succeeds with valid parameters") {
        val controller = createMockController
        controller.prepareParams(
          "first_name" -> "dummy",
          "middle_name" -> "dummy",
          "last_name" -> "dummy",
          "gender" -> Int.MaxValue.toString(),
          "started_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
          "left_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
          "other_detail" -> "dummy")
        controller.createResource()
        controller.status should equal(200)
      }

      it("fails with invalid parameters") {
        val controller = createMockController
        controller.prepareParams() // no parameters
        controller.createResource()
        controller.status should equal(400)
        controller.errorMessages.size should be > (0)
      }
    }

    it("shows a resource edit input form") {
      val employee = newEmployee
      val controller = createMockController
      controller.editResource(employee.id)
      controller.status should equal(200)
      controller.renderCall.map(_.path) should equal(Some("/employees/edit"))
    }

    it("updates a employee") {
      val employee = newEmployee
      val controller = createMockController
      controller.prepareParams(
        "first_name" -> "dummy",
        "middle_name" -> "dummy",
        "last_name" -> "dummy",
        "gender" -> Int.MaxValue.toString(),
        "started_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
        "left_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
        "other_detail" -> "dummy")
      controller.updateResource(employee.id)
      controller.status should equal(200)
    }

    it("destroys a employee") {
      val employee = newEmployee
      val controller = createMockController
      controller.destroyResource(employee.id)
      controller.status should equal(200)
    }

  }

}

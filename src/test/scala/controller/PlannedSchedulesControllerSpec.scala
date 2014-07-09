package controller

import org.scalatest.{ Matchers, FunSpec }
import skinny._
import skinny.test._
import org.joda.time._
import model._

// NOTICE before/after filters won't be executed by default
class PlannedSchedulesControllerSpec extends FunSpec with Matchers with DBSettings {

  def createMockController = new PlannedSchedulesController with MockController
  def newPlannedSchedule = FactoryGirl(PlannedSchedule).create()

  describe("PlannedSchedulesController") {

    describe("shows planned schedules") {
      it("shows HTML response") {
        val controller = createMockController
        controller.showResources()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/plannedSchedules/index"))
        controller.contentType should equal("text/html; charset=utf-8")
      }

      it("shows JSON response") {
        implicit val format = Format.JSON
        val controller = createMockController
        controller.showResources()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/plannedSchedules/index"))
        controller.contentType should equal("application/json; charset=utf-8")
      }
    }

    describe("shows a planned schedule") {
      it("shows HTML response") {
        val plannedSchedule = newPlannedSchedule
        val controller = createMockController
        controller.showResource(plannedSchedule.id)
        controller.status should equal(200)
        controller.requestScope[PlannedSchedule]("item") should equal(Some(plannedSchedule))
        controller.renderCall.map(_.path) should equal(Some("/plannedSchedules/show"))
      }
    }

    describe("shows new resource input form") {
      it("shows HTML response") {
        val controller = createMockController
        controller.newResource()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/plannedSchedules/new"))
      }
    }

    describe("creates a planned schedule") {
      it("succeeds with valid parameters") {
        val controller = createMockController
        controller.prepareParams(
          "name" -> "dummy",
          "description" -> "dummy",
          "other_details" -> "dummy")
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
      val plannedSchedule = newPlannedSchedule
      val controller = createMockController
      controller.editResource(plannedSchedule.id)
      controller.status should equal(200)
      controller.renderCall.map(_.path) should equal(Some("/plannedSchedules/edit"))
    }

    it("updates a planned schedule") {
      val plannedSchedule = newPlannedSchedule
      val controller = createMockController
      controller.prepareParams(
        "name" -> "dummy",
        "description" -> "dummy",
        "other_details" -> "dummy")
      controller.updateResource(plannedSchedule.id)
      controller.status should equal(200)
    }

    it("destroys a planned schedule") {
      val plannedSchedule = newPlannedSchedule
      val controller = createMockController
      controller.destroyResource(plannedSchedule.id)
      controller.status should equal(200)
    }

  }

}

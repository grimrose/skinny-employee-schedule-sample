package controller

import org.scalatest.{Matchers, FunSpec}
import skinny._
import skinny.test._
import org.joda.time._
import model._

// NOTICE before/after filters won't be executed by default
class SchedulesControllerSpec extends FunSpec with Matchers with DBSettings {

  def createMockController = new SchedulesController with MockController
  def newSchedule = FactoryGirl(Schedule).create()

  describe("SchedulesController") {

    describe("shows schedules") {
      it("shows HTML response") {
        val controller = createMockController
        controller.showResources()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/schedules/index"))
        controller.contentType should equal("text/html; charset=utf-8")
      }

      it("shows JSON response") {
        implicit val format = Format.JSON
        val controller = createMockController
        controller.showResources()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/schedules/index"))
        controller.contentType should equal("application/json; charset=utf-8")
      }
    }

    describe("shows a schedule") {
      it("shows HTML response") {
        val schedule = newSchedule
        val controller = createMockController
        controller.showResource(schedule.id)
        controller.status should equal(200)
        controller.requestScope[Schedule]("item") should equal(Some(schedule))
        controller.renderCall.map(_.path) should equal(Some("/schedules/show"))
      }
    }

    describe("shows new resource input form") {
      it("shows HTML response") {
        val controller = createMockController
        controller.newResource()
        controller.status should equal(200)
        controller.renderCall.map(_.path) should equal(Some("/schedules/new"))
      }
    }

    describe("creates a schedule") {
      it("succeeds with valid parameters") {
        val controller = createMockController
        controller.prepareParams(
          "planned_schedule_id" -> Long.MaxValue.toString(),
          "start" -> skinny.util.DateTimeUtil.toString(new DateTime()),
          "end" -> skinny.util.DateTimeUtil.toString(new DateTime()),
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
      val schedule = newSchedule
      val controller = createMockController
      controller.editResource(schedule.id)
      controller.status should equal(200)
      controller.renderCall.map(_.path) should equal(Some("/schedules/edit"))
    }

    it("updates a schedule") {
      val schedule = newSchedule
      val controller = createMockController
      controller.prepareParams(
        "planned_schedule_id" -> Long.MaxValue.toString(),
        "start" -> skinny.util.DateTimeUtil.toString(new DateTime()),
        "end" -> skinny.util.DateTimeUtil.toString(new DateTime()),
        "other_details" -> "dummy")
      controller.updateResource(schedule.id)
      controller.status should equal(200)
    }

    it("destroys a schedule") {
      val schedule = newSchedule
      val controller = createMockController
      controller.destroyResource(schedule.id)
      controller.status should equal(200)
    }

  }

}

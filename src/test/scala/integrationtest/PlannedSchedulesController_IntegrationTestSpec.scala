package integrationtest

import org.scalatra.test.scalatest._
import skinny._
import skinny.test._
import org.joda.time._
import _root_.controller.Controllers
import model._

class PlannedSchedulesController_IntegrationTestSpec extends ScalatraFlatSpec with SkinnyTestSupport with DBSettings {
  addFilter(Controllers.plannedSchedules, "/*")

  def newPlannedSchedule = FactoryGirl(PlannedSchedule).create()

  it should "show planned schedules" in {
    get("/planned_schedules") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/planned_schedules/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/planned_schedules.json") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/planned_schedules.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show a planned schedule in detail" in {
    get(s"/planned_schedules/${newPlannedSchedule.id}") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/planned_schedules/${newPlannedSchedule.id}.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/planned_schedules/${newPlannedSchedule.id}.json") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show new entry form" in {
    get(s"/planned_schedules/new") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "create a planned schedule" in {
    post(s"/planned_schedules",
      "name" -> "dummy",
      "description" -> "dummy",
      "other_details" -> "dummy") {
      logBodyUnless(403)
      status should equal(403)
    }

    withSession("csrf-token" -> "valid_token") {
      post(s"/planned_schedules",
        "name" -> "dummy",
        "description" -> "dummy",
        "other_details" -> "dummy",
        "csrf-token" -> "valid_token") {
        logBodyUnless(302)
        status should equal(302)
        val id = header("Location").split("/").last.toLong
        PlannedSchedule.findById(id).isDefined should equal(true)
      }
    }
  }

  it should "show the edit form" in {
    get(s"/planned_schedules/${newPlannedSchedule.id}/edit") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "update a planned schedule" in {
    put(s"/planned_schedules/${newPlannedSchedule.id}",
      "name" -> "dummy",
      "description" -> "dummy",
      "other_details" -> "dummy") {
      logBodyUnless(403)
      status should equal(403)
    }

    withSession("csrf-token" -> "valid_token") {
      put(s"/planned_schedules/${newPlannedSchedule.id}",
        "name" -> "dummy",
        "description" -> "dummy",
        "other_details" -> "dummy",
        "csrf-token" -> "valid_token") {
        logBodyUnless(302)
        status should equal(302)
      }
    }
  }

  it should "delete a planned schedule" in {
    delete(s"/planned_schedules/${newPlannedSchedule.id}") {
      logBodyUnless(403)
      status should equal(403)
    }
    withSession("csrf-token" -> "valid_token") {
      delete(s"/planned_schedules/${newPlannedSchedule.id}?csrf-token=valid_token") {
        logBodyUnless(200)
        status should equal(200)
      }
    }
  }

}

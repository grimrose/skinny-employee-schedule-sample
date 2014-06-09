package integrationtest

import org.scalatra.test.scalatest._
import skinny._
import skinny.test._
import org.joda.time._
import _root_.controller.Controllers
import model._

class SchedulesController_IntegrationTestSpec extends ScalatraFlatSpec with SkinnyTestSupport with DBSettings {
  addFilter(Controllers.schedules, "/*")

  def newSchedule = FactoryGirl(Schedule).create()

  it should "show schedules" in {
    get("/schedules") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/schedules/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/schedules.json") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/schedules.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show a schedule in detail" in {
    get(s"/schedules/${newSchedule.id}") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/schedules/${newSchedule.id}.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/schedules/${newSchedule.id}.json") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show new entry form" in {
    get(s"/schedules/new") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "create a schedule" in {
    post(s"/schedules",
      "planned_schedule_id" -> Long.MaxValue.toString(),
      "start" -> skinny.util.DateTimeUtil.toString(new DateTime()),
      "end" -> skinny.util.DateTimeUtil.toString(new DateTime()),
      "other_details" -> "dummy") {
        logBodyUnless(403)
        status should equal(403)
      }

    withSession("csrf-token" -> "valid_token") {
      post(s"/schedules",
        "planned_schedule_id" -> Long.MaxValue.toString(),
        "start" -> skinny.util.DateTimeUtil.toString(new DateTime()),
        "end" -> skinny.util.DateTimeUtil.toString(new DateTime()),
        "other_details" -> "dummy",
        "csrf-token" -> "valid_token") {
          logBodyUnless(302)
          status should equal(302)
          val id = header("Location").split("/").last.toLong
          Schedule.findById(id).isDefined should equal(true)
        }
    }
  }

  it should "show the edit form" in {
    get(s"/schedules/${newSchedule.id}/edit") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "update a schedule" in {
    put(s"/schedules/${newSchedule.id}",
      "planned_schedule_id" -> Long.MaxValue.toString(),
      "start" -> skinny.util.DateTimeUtil.toString(new DateTime()),
      "end" -> skinny.util.DateTimeUtil.toString(new DateTime()),
      "other_details" -> "dummy") {
        logBodyUnless(403)
        status should equal(403)
      }

    withSession("csrf-token" -> "valid_token") {
      put(s"/schedules/${newSchedule.id}",
        "planned_schedule_id" -> Long.MaxValue.toString(),
        "start" -> skinny.util.DateTimeUtil.toString(new DateTime()),
        "end" -> skinny.util.DateTimeUtil.toString(new DateTime()),
        "other_details" -> "dummy",
        "csrf-token" -> "valid_token") {
          logBodyUnless(302)
          status should equal(302)
        }
    }
  }

  it should "delete a schedule" in {
    delete(s"/schedules/${newSchedule.id}") {
      logBodyUnless(403)
      status should equal(403)
    }
    withSession("csrf-token" -> "valid_token") {
      delete(s"/schedules/${newSchedule.id}?csrf-token=valid_token") {
        logBodyUnless(200)
        status should equal(200)
      }
    }
  }

}

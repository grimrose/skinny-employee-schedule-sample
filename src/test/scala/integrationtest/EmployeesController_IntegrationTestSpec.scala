package integrationtest

import org.scalatra.test.scalatest._
import skinny._
import skinny.test._
import org.joda.time._
import _root_.controller.Controllers
import model._

class EmployeesController_IntegrationTestSpec extends ScalatraFlatSpec with SkinnyTestSupport with DBSettings {
  addFilter(Controllers.employees, "/*")

  def newEmployee = FactoryGirl(Employee).create()

  it should "show employees" in {
    get("/employees") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/employees/") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/employees.json") {
      logBodyUnless(200)
      status should equal(200)
    }
    get("/employees.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show a employee in detail" in {
    get(s"/employees/${newEmployee.id}") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/employees/${newEmployee.id}.xml") {
      logBodyUnless(200)
      status should equal(200)
    }
    get(s"/employees/${newEmployee.id}.json") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "show new entry form" in {
    get(s"/employees/new") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "create a employee" in {
    post(s"/employees",
      "first_name" -> "dummy",
      "middle_name" -> "dummy",
      "last_name" -> "dummy",
      "gender" -> Int.MaxValue.toString(),
      "started_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
      "left_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
      "other_detail" -> "dummy") {
        logBodyUnless(403)
        status should equal(403)
      }

    withSession("csrf-token" -> "valid_token") {
      post(s"/employees",
        "first_name" -> "dummy",
        "middle_name" -> "dummy",
        "last_name" -> "dummy",
        "gender" -> Int.MaxValue.toString(),
        "started_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
        "left_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
        "other_detail" -> "dummy",
        "csrf-token" -> "valid_token") {
          logBodyUnless(302)
          status should equal(302)
          val id = header("Location").split("/").last.toLong
          Employee.findById(id).isDefined should equal(true)
        }
    }
  }

  it should "show the edit form" in {
    get(s"/employees/${newEmployee.id}/edit") {
      logBodyUnless(200)
      status should equal(200)
    }
  }

  it should "update a employee" in {
    put(s"/employees/${newEmployee.id}",
      "first_name" -> "dummy",
      "middle_name" -> "dummy",
      "last_name" -> "dummy",
      "gender" -> Int.MaxValue.toString(),
      "started_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
      "left_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
      "other_detail" -> "dummy") {
        logBodyUnless(403)
        status should equal(403)
      }

    withSession("csrf-token" -> "valid_token") {
      put(s"/employees/${newEmployee.id}",
        "first_name" -> "dummy",
        "middle_name" -> "dummy",
        "last_name" -> "dummy",
        "gender" -> Int.MaxValue.toString(),
        "started_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
        "left_employment" -> skinny.util.DateTimeUtil.toString(new LocalDate()),
        "other_detail" -> "dummy",
        "csrf-token" -> "valid_token") {
          logBodyUnless(302)
          status should equal(302)
        }
    }
  }

  it should "delete a employee" in {
    delete(s"/employees/${newEmployee.id}") {
      logBodyUnless(403)
      status should equal(403)
    }
    withSession("csrf-token" -> "valid_token") {
      delete(s"/employees/${newEmployee.id}?csrf-token=valid_token") {
        logBodyUnless(200)
        status should equal(200)
      }
    }
  }

}

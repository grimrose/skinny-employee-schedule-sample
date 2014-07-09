package model

import org.scalatest.{ Matchers, fixture }
import scalikejdbc._
import scalikejdbc.scalatest._
import skinny.DBSettings
import skinny.orm.Alias
import skinny.test._

class PlannedScheduleSpec extends fixture.FunSpec with Matchers with DBSettings with AutoRollback {

  describe("PlannedSchedule") {
    it("should insert") { implicit session =>
      val plannedSchedule = FactoryGirl.apply(PlannedSchedule).create()
      PlannedSchedule.findById(plannedSchedule.id).isDefined should equal(true)
    }
    it("should update") { implicit session =>
      val id = FactoryGirl.apply(PlannedSchedule).create().id
      PlannedSchedule.updateById(id).withAttributes('name -> "schedule2") should be(1)
      PlannedSchedule.findById(id).get.name should be("schedule2")
    }
    it("should find by name") { implicit session =>
      val id = FactoryGirl.apply(PlannedSchedule).create().id
      PlannedSchedule.updateById(id).withAttributes('name -> "schedule3")
      val p: Alias[PlannedSchedule] = PlannedSchedule.defaultAlias
      PlannedSchedule.findBy(sqls.eq(p.name, "schedule3")).size should be(1)
    }
  }

}

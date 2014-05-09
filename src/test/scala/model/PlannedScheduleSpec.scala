package model

import skinny.{ParamType, DBSettings}
import skinny.test._
import org.scalatest.fixture.FlatSpec
import scalikejdbc._, SQLInterpolation._
import scalikejdbc.scalatest._
import org.joda.time._
import org.scalatest.fixture
import org.scalatest.matchers.ShouldMatchers
import skinny.orm.Alias

class PlannedScheduleSpec extends fixture.FunSpec with ShouldMatchers with DBSettings with AutoRollback {
  override def fixture(implicit session: DBSession): Unit = {
    delete.from(PlannedSchedule).toSQL.execute().apply()
  }

  describe("PlannedSchedule") {
    it("should insert") { implicit session =>
      FactoryGirl.apply(PlannedSchedule).create()
      PlannedSchedule.count() should be(1)
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

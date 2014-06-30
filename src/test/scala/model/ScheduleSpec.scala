package model

import org.joda.time._
import org.scalatest.{ Matchers, fixture }
import scalikejdbc._
import scalikejdbc.scalatest._
import skinny.DBSettings
import skinny.test._

class ScheduleSpec extends fixture.FunSpec with Matchers with DBSettings with AutoRollback {
  override def fixture(implicit session: DBSession): Unit = {
    deleteFrom(EmployeeSchedule).toSQL.execute().apply()
    delete.from(PlannedSchedule).toSQL.execute().apply()
    delete.from(Schedule).toSQL.execute().apply()
  }

  describe("Schedule") {
    it("should insert") { implicit session =>
      val id = FactoryGirl.apply(PlannedSchedule).create().id
      FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> id).create()
      val s = Schedule.defaultAlias
      Schedule.findBy(sqls.eq(s.plannedScheduleId, id)).size should be(1)
    }
    it("should update") { implicit session =>
      val id = FactoryGirl.apply(PlannedSchedule).withAttributes('name -> "planned").create().id
      val scheduleId: Long = FactoryGirl.apply(Schedule).withAttributes('plannedScheduleId -> id).create().id
      val now: DateTime = DateTime.now()
      Schedule.updateById(scheduleId).withAttributes('end -> now)

      val actual: Option[Schedule] = Schedule.findById(scheduleId)
      actual.flatMap(_.end) should be(Some(now))
      actual.flatMap(_.plannedSchedule).fold("")(_.name) should be("planned")
    }
  }

}

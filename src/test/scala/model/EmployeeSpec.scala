package model

import org.joda.time._
import org.scalatest.{ Matchers, fixture }
import scalikejdbc._
import scalikejdbc.scalatest._
import skinny.DBSettings
import skinny.orm.Alias
import skinny.test._

class EmployeeSpec extends fixture.FunSpec with Matchers with DBSettings with AutoRollback {
  override def fixture(implicit session: DBSession): Unit = {
    deleteFrom(EmployeeSchedule).toSQL.execute().apply()
    delete.from(Employee).toSQL.execute().apply()
  }

  describe("Employee") {
    it("should insert") { implicit session =>
      FactoryGirl.apply(Employee).create()
      Employee.count() should be(1)
    }
    it("should update") { implicit session =>
      val id: Long = FactoryGirl.apply(Employee).create().id
      val aprilFirst = LocalDate.parse("2014-04-01")
      Employee.updateById(id).withAttributes('startedEmployment -> aprilFirst)
      val e: Alias[Employee] = Employee.defaultAlias
      Employee.where(sqls.eq(e.startedEmployment, aprilFirst)).count() should be(1)
    }
  }
}

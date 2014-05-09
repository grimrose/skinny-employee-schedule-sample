package model

import skinny.DBSettings
import skinny.test._
import org.scalatest.fixture.FlatSpec
import scalikejdbc._, SQLInterpolation._
import scalikejdbc.scalatest._
import org.joda.time._
import org.scalatest.fixture
import org.scalatest.matchers.ShouldMatchers
import skinny.orm.Alias

class EmployeeSpec extends fixture.FunSpec with ShouldMatchers with DBSettings with AutoRollback {
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

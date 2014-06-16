package controller

import skinny._
import skinny.validator._
import model.{ EmployeeSchedule, Schedule, Employee }
import skinny.controller.feature.RequestScopeFeature

class AssignmentsController extends ApplicationController {
  protectFromForgery()

  beforeAction() {
    set(RequestScopeFeature.ATTR_RESOURCES_NAME -> "items")
    set(RequestScopeFeature.ATTR_RESOURCE_NAME -> "item")
    set("schedules", Schedule.findAll())
    set("employees", Employee.findAll())
  }

  def resourcesName = "assignments"

  def resourceName = "assignment"

  def resourcesBasePath = s"/${toSnakeCase(resourcesName)}"

  def viewsDirectoryPath = s"/${resourcesName}"

  protected def enablePagination = true

  def index = render(s"${viewsDirectoryPath}/index")

  def employeeAction()(implicit format: Format = Format.HTML): Any = withFormat(format) {
    if (enablePagination) {
      val pageNo: Int = params.getAs[Int]("page").getOrElse(1)
      val pageSize: Int = 20
      val totalCount: Long = Employee.countAllModels()
      val totalPages: Int = (totalCount / pageSize).toInt + (if (totalCount % pageSize == 0) 0 else 1)
      set("employees", Employee.findModels(pageSize, pageNo))
      set("totalPages" -> totalPages)
    } else {
      set("employees", Employee.findAll())
    }
    render(s"${viewsDirectoryPath}/employees/index")
  }

  def employeeShowAction = {
    val employeeId = params.getAsOrElse[Long]("employeeId", -1)
    showEmployeeResource(employeeId)
  }

  def showEmployeeResource(employeeId: Long)(implicit format: Format = Format.HTML): Any = withFormat(format) {
    set("employee", Employee.joins(Employee.schedulesRef).findById(employeeId).getOrElse(haltWithBody(404)))
    render(s"${viewsDirectoryPath}/employees/show")
  }

  def employeeNewAction = {
    val employeeId = params.getAsOrElse[Long]("employeeId", -1)
    newEmployeeResource(employeeId)
  }

  def newEmployeeResource(employeeId: Long) = {
    set("employee", Employee.findById(employeeId).getOrElse(haltWithBody(404)))
    render(s"${viewsDirectoryPath}/employees/new")
  }

  def destroyAction = {
    val employeeId = params.getAsOrElse[Long]("employeeId", -1)
    val scheduleId = params.getAsOrElse[Long]("scheduleId", -1)
    destroyResource(employeeId, scheduleId)
  }

  def destroyResource(employeeId: Long, scheduleId: Long) = {
    EmployeeSchedule.findByEmployeeIdAndScheduleId(employeeId, scheduleId).map { item =>
      EmployeeSchedule.deleteByEmployeeIdAndScheduleId(employeeId, scheduleId)
      flash += ("notice" -> createI18n().get(s"${resourceName}.flash.deleted").getOrElse(s"The ${resourceName} was deleted."))
      status = 200
    } getOrElse haltWithBody(404)
  }

}
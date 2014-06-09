package controller

import skinny._
import skinny.validator._
import model.{ Employee, Schedule, EmployeeSchedule }
import skinny.controller.feature.RequestScopeFeature

class EmployeesSchedulesController extends ApplicationController {
  protectFromForgery()

  beforeAction() {
    set(RequestScopeFeature.ATTR_RESOURCES_NAME -> "items")
    set(RequestScopeFeature.ATTR_RESOURCE_NAME -> "item")
    set("schedules", Schedule.findAll())
    set("employees", Employee.findAll())
  }

  def resourcesName = "employeesSchedules"

  def resourceName = "employeeSchedule"

  def resourcesBasePath = s"/${toSnakeCase(resourcesName)}"

  def viewsDirectoryPath = s"/${resourcesName}"

  protected def enablePagination = true

  def showResources()(implicit format: Format = Format.HTML): Any = withFormat(format) {
    if (enablePagination) {
      val pageNo: Int = params.getAs[Int]("page").getOrElse(1)
      val pageSize: Int = 20
      val totalCount: Long = EmployeeSchedule.count()
      val totalPages: Int = (totalCount / pageSize).toInt + (if (totalCount % pageSize == 0) 0 else 1)
      set("items", EmployeeSchedule.findAllWithPagination(Pagination.page(pageNo).per(pageSize)))
      set("totalPages" -> totalPages)
    } else {
      set("items", EmployeeSchedule.findAll())
    }
    render(s"${viewsDirectoryPath}/index")
  }

  def indexApiAction = (for {
    ext <- params.get("ext")
  } yield {
    ext match {
      case "json" => showResources()(Format.JSON)
      case "xml" => showResources()(Format.XML)
      case _ => haltWithBody(404)
    }
  }) getOrElse haltWithBody(404)

  def showResource(employeeId: Long, scheduleId: Long)(implicit format: Format = Format.HTML): Any = withFormat(format) {
    set("item", EmployeeSchedule.findByEmployeeIdAndScheduleId(employeeId, scheduleId).getOrElse(haltWithBody(404)))
    render(s"${viewsDirectoryPath}/show")
  }

  def showAction = {
    val employeeId = params.getAsOrElse[Long]("employeeId", -1)
    val scheduleId = params.getAsOrElse[Long]("scheduleId", -1)
    showResource(employeeId, scheduleId)
  }

  def showApiAction = (for {
    employeeId <- params.getAs[Long]("employeeId")
    scheduleId <- params.getAs[Long]("scheduleId")
    ext <- params.get("ext")
  } yield {
    ext match {
      case "json" => showResource(employeeId, scheduleId)(Format.JSON)
      case "xml" => showResource(employeeId, scheduleId)(Format.XML)
      case _ => haltWithBody(404)
    }
  }) getOrElse haltWithBody(404)

  def newResource = render(s"${viewsDirectoryPath}/new")

  def createParams: Params = Params(params)

  def createForm = validation(createParams,
    paramKey("employee_id") is required & numeric & longValue,
    paramKey("schedule_id") is required & numeric & longValue
  )

  def createFormStrongParameters = Seq(
    "employee_id" -> ParamType.Long,
    "schedule_id" -> ParamType.Long
  )

  def createResource(implicit format: Format = Format.HTML): Any = withFormat(format) {
    debugLoggingParameters(createForm)
    if (createForm.validate()) {
      val parameters = createParams.permit(createFormStrongParameters: _*)
      debugLoggingPermittedParameters(parameters)
      val ids: Map[String, Long] = EmployeeSchedule.createWithStrongParameters(parameters)

      logger.debug(ids)

      flash += ("notice" -> createI18n().get(s"${resourceName}.flash.created").getOrElse(s"The ${resourceName} was created."))
      redirect302(s"${resourcesBasePath}/employees/${ids.getOrElse("employee_id", -1)}/schedules/${ids.getOrElse("schedule_id", -1)}")
    } else {
      status = 400
      render(s"${viewsDirectoryPath}/new")
    }
  }

  def destroyResource(employeeId: Long, scheduleId: Long) = {
    EmployeeSchedule.findByEmployeeIdAndScheduleId(employeeId, scheduleId).map { item =>
      EmployeeSchedule.deleteByEmployeeIdAndScheduleId(employeeId, scheduleId)
      flash += ("notice" -> createI18n().get(s"${resourceName}.flash.deleted").getOrElse(s"The ${resourceName} was deleted."))
      status = 200
    } getOrElse haltWithBody(404)
  }

  def destroyAction = {
    val employeeId = params.getAsOrElse[Long]("employeeId", -1)
    val scheduleId = params.getAsOrElse[Long]("scheduleId", -1)
    destroyResource(employeeId, scheduleId)
  }

  def debugLoggingParameters(form: MapValidator, id: Option[Long] = None) = {
    val forId = id.map { id => s" for [id -> ${id}]" }.getOrElse("")
    val params = form.paramMap.map { case (name, value) => s"${name} -> '${value}'" }.mkString("[", ", ", "]")
    logger.debug(s"Parameters${forId}: ${params}")
  }

  def debugLoggingPermittedParameters(parameters: PermittedStrongParameters, id: Option[Long] = None) = {
    val forId = id.map { id => s" for [id -> ${id}]" }.getOrElse("")
    val params = parameters.params.map { case (name, (v, t)) => s"${name} -> '${v}' as ${t}" }.mkString("[", ", ", "]")
    logger.debug(s"Permitted parameters${forId}: ${params}")
  }

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
    set("employee", Employee.findById(employeeId).getOrElse(haltWithBody(404)))
    render(s"${viewsDirectoryPath}/employees/new")
  }

  //  def showScheduleResource(scheduleId: Long)(implicit format: Format = Format.HTML): Any = withFormat(format) {
  //    render(s"/${resourcesName}/schedules/index")
  //  }
  //
  //  def scheduleAction = {
  //    val scheduleId = params.getAsOrElse[Long]("scheduleId", -1)
  //    showScheduleResource(scheduleId)
  //  }
}

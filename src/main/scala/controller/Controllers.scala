package controller

import skinny._
import skinny.controller.AssetsController

object Controllers {

  def mount(ctx: ServletContext): Unit = {
    employeesSchedules.mount(ctx)
    schedules.mount(ctx)
    plannedSchedules.mount(ctx)
    employees.mount(ctx)
    root.mount(ctx)
    AssetsController.mount(ctx)
  }

  object root extends RootController with Routes {
    val indexUrl = get("/?")(index).as('index)
  }

  object employees extends _root_.controller.EmployeesController {
  }

  object plannedSchedules extends _root_.controller.PlannedSchedulesController {
  }

  object schedules extends _root_.controller.SchedulesController {
  }

  object employeesSchedules extends _root_.controller.EmployeesSchedulesController with Routes {
    val indexUrl = get(s"${resourcesBasePath}")(showResources).as('index)
    val indexWithSlashUrl = get(s"${resourcesBasePath}/")(showResources).as('index)
    val indexApiUrl = get(s"${resourcesBasePath}.:ext")(indexApiAction).as('indexApi)

    val showUrl = get(s"${resourcesBasePath}/employees/:employeeId/schedules/:scheduleId")(showAction).as('show)
    val showApiUrl = get(s"${resourcesBasePath}/employees/:employeeId/schedules/:scheduleId.:ext")(showApiAction).as('showApi)

    val newUrl = get(s"${resourcesBasePath}/new")(newResource).as('new)

    val createUrl = post(s"${resourcesBasePath}")(createResource).as('create)

    val destroyUrl = delete(s"${resourcesBasePath}/employees/:employeeId/schedules/:scheduleId")(destroyAction).as('destroy)

    val employeesUrl = get(s"${resourcesBasePath}/employees")(employeeAction).as('employees)
    val employeesWithSlashUrl = get(s"${resourcesBasePath}/employees/")(employeeAction).as('employees)
    val employeesShowUrl = get(s"${resourcesBasePath}/employees/:employeeId/schedules")(employeeShowAction).as('employeesShow)
    val employeesNewUrl = get(s"${resourcesBasePath}/employees/:employeeId/schedules/new")(employeeNewAction).as('employeesNew)
    val employeesCreateUrl = post(s"${resourcesBasePath}/employees/:employeeId/schedules")(createResource).as('employeesCreate)

//    val schedulesUrl = get("/employees_schedules/schedules/")(scheduleAction).as('schedules)
  }

}

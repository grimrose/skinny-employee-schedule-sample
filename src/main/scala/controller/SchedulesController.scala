package controller

import skinny._
import skinny.validator._
import model.{PlannedSchedule, Schedule}
import skinny.controller.Params

class SchedulesController extends SkinnyResource with ApplicationController {
  protectFromForgery()

  override def model = Schedule

  override def resourcesName = "schedules"

  override def resourceName = "schedule"

  override def resourcesBasePath = s"/${toSnakeCase(resourcesName)}"

  override def useSnakeCasedParamKeys = true

  override def viewsDirectoryPath = s"/${resourcesName}"

  override def createParams = Params(params).withDateTime("start").withDateTime("end")

  override def createForm = validation(createParams,
    paramKey("planned_schedule_id") is required & numeric & longValue,
    paramKey("start") is required & dateTimeFormat,
    paramKey("end") is dateTimeFormat,
    paramKey("other_details") is maxLength(512)
  )

  override def createFormStrongParameters = Seq(
    "planned_schedule_id" -> ParamType.Long,
    "start" -> ParamType.DateTime,
    "end" -> ParamType.DateTime,
    "other_details" -> ParamType.String
  )

  override def updateParams = {
    val p = Params(params).withDateTime("start").withDateTime("end")
    p.selectDynamic("end") match {
      case Some(_) => p
      case None => Params(p.underlying + ("end" -> null))
    }
  }

  override def updateForm = validation(updateParams,
    paramKey("planned_schedule_id") is required & numeric & longValue,
    paramKey("start") is required & dateTimeFormat,
    paramKey("end") is dateTimeFormat,
    paramKey("other_details") is maxLength(512)
  )

  override def updateFormStrongParameters = Seq(
    "planned_schedule_id" -> ParamType.Long,
    "start" -> ParamType.DateTime,
    "end" -> ParamType.DateTime,
    "other_details" -> ParamType.String
  )

  beforeAction(only = Seq('index, 'indexWithSlash, 'new, 'create, 'createWithSlash, 'edit, 'update)) {
    set("plannedSchedules", PlannedSchedule.findAll())
  }

}

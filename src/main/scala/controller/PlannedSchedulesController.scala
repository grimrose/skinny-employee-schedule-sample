package controller

import skinny._
import skinny.validator._
import _root_.controller._
import model.PlannedSchedule

class PlannedSchedulesController extends SkinnyResource with ApplicationController {
  protectFromForgery()

  override def model = PlannedSchedule
  override def resourcesName = "plannedSchedules"
  override def resourceName = "plannedSchedule"

  override def resourcesBasePath = s"/${toSnakeCase(resourcesName)}"
  override def useSnakeCasedParamKeys = true

  override def viewsDirectoryPath = s"/${resourcesName}"

  override def createParams = Params(params)
  override def createForm = validation(createParams,
    paramKey("name") is required & maxLength(512),
    paramKey("description") is maxLength(512),
    paramKey("other_details") is maxLength(512)
  )
  override def createFormStrongParameters = Seq(
    "name" -> ParamType.String,
    "description" -> ParamType.String,
    "other_details" -> ParamType.String
  )

  override def updateParams = Params(params)
  override def updateForm = validation(updateParams,
    paramKey("name") is required & maxLength(512),
    paramKey("description") is maxLength(512),
    paramKey("other_details") is maxLength(512)
  )
  override def updateFormStrongParameters = Seq(
    "name" -> ParamType.String,
    "description" -> ParamType.String,
    "other_details" -> ParamType.String
  )

}

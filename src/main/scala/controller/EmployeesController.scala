package controller

import skinny._
import skinny.validator._
import model.{Gender, Employee}

class EmployeesController extends SkinnyResource with ApplicationController {
  protectFromForgery()

  override def model = Employee

  override def resourcesName = "employees"

  override def resourceName = "employee"

  override def resourcesBasePath = s"/${toSnakeCase(resourcesName)}"

  override def useSnakeCasedParamKeys = true

  override def viewsDirectoryPath = s"/${resourcesName}"

  override def createParams = Params(params).withDate("started_employment").withDate("left_employment")

  override def createForm = validation(createParams,
    paramKey("first_name") is required & maxLength(512),
    paramKey("middle_name") is required & maxLength(512),
    paramKey("last_name") is required & maxLength(512),
    paramKey("gender") is required & numeric & intValue,
    paramKey("started_employment") is dateFormat,
    paramKey("left_employment") is dateFormat,
    paramKey("other_detail") is maxLength(512)
  )

  override def createFormStrongParameters = Seq(
    "first_name" -> ParamType.String,
    "middle_name" -> ParamType.String,
    "last_name" -> ParamType.String,
    "gender" -> ParamType.Int,
    "started_employment" -> ParamType.LocalDate,
    "left_employment" -> ParamType.LocalDate,
    "other_detail" -> ParamType.String
  )

  override def updateParams = {
    val p: Params = Params(params).withDate("started_employment").withDate("left_employment")
    val p1 = p.selectDynamic("started_employment") match {
      case Some(_) => p
      case None => Params(p.underlying + ("started_employment" -> null))
    }
    p1.selectDynamic("left_employment") match {
      case Some(_) => p1
      case None => Params(p1.underlying + ("left_employment" -> null))
    }
  }

  override def updateForm = validation(updateParams,
    paramKey("first_name") is required & maxLength(512),
    paramKey("middle_name") is required & maxLength(512),
    paramKey("last_name") is required & maxLength(512),
    paramKey("gender") is required & numeric & intValue,
    paramKey("started_employment") is dateFormat,
    paramKey("left_employment") is dateFormat,
    paramKey("other_detail") is maxLength(512)
  )

  override def updateFormStrongParameters = Seq(
    "first_name" -> ParamType.String,
    "middle_name" -> ParamType.String,
    "last_name" -> ParamType.String,
    "gender" -> ParamType.Int,
    "started_employment" -> ParamType.LocalDate,
    "left_employment" -> ParamType.LocalDate,
    "other_detail" -> ParamType.String
  )

  beforeAction(only = Seq('index, 'indexWithSlash, 'new, 'create, 'createWithSlash, 'edit, 'update)) {
    set("genderElements", Gender.all())
  }
}

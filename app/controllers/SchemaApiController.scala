package controllers

import core.store.SchemaStore
import core.validator.SchemaValidator
import javax.inject.Inject
import models.ApiModels.Response
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc._

import scala.util.Success

class SchemaApiController @Inject()(cc: ControllerComponents, schemaStore: SchemaStore[JsValue], validator: SchemaValidator[JsValue, JsValue]) extends AbstractController(cc) {

  def uploadSchema(schemaId: String) = Action(parse.json) { request =>
    schemaStore.upload(schemaId, request.body) match {
      case Success(_) =>
        Created(Json.toJson(
          Response("uploadSchema", schemaId, "success")
        ))
      case _ => InternalServerError
    }
  }

  def readSchema(schemaId: String) = Action {
    schemaStore.read(schemaId) match {
      case Success(schema) =>
        Ok(schema)
      case _ =>
        NotFound
    }
  }

  def validateDocument(schemaId: String) = Action(parse.json) { request =>
    schemaStore.read(schemaId) match {
      case Success(schema) =>
        validate(schemaId, request.body, schema)
      case _ =>
        NotFound
    }
  }

  private def validate(schemaId: String, data: JsValue, schema: JsValue) = {
    validator.validate(withoutNull(data), schema) match {
      case Right(_) =>
        Ok(Json.toJson(
          Response("validateDocument", schemaId, "success")
        ))
      case Left(errorList) =>
        BadRequest(Json.toJson(
          Response("validateDocument", schemaId, "error", Some(errorList.mkString(",")))
        ))
    }
  }

  private def withoutNull(json: JsValue): JsValue = json match {
    case JsObject(fields) =>
      JsObject(fields.flatMap {
        case (_, JsNull) => None
        case other => Some(other._1, withoutNull(other._2))
      })
    case other => other
  }
}

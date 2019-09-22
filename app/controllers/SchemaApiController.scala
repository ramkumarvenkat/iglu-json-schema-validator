package controllers

import com.snowplowanalytics.iglu.core.{SchemaKey, SchemaMap, SchemaVer, SelfDescribingSchema}
import core.store.SchemaStore
import core.validator.SchemaValidator
import javax.inject.Inject
import models.ApiModels.Response
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}
import play.api.mvc._

class SchemaApiController @Inject()(cc: ControllerComponents, schemaStore: SchemaStore[JsValue], validator: SchemaValidator[JsValue, JsValue]) extends AbstractController(cc) {

  def uploadSchema(schemaId: String) = Action(parse.json) { request =>
    schemaStore.read(schemaId) match {
      case Some(schema) =>
        upsert(schemaId, request.body, schema)
      case _ =>
        insert(schemaId, request.body)
    }

    Created(Json.toJson(
      Response("uploadSchema", schemaId, "success")
    ))
  }

  private def upsert(schemaId: String, schema: JsValue, existingSchema: SelfDescribingSchema[JsValue]) = {
    val version = existingSchema.self.schemaKey.version
    val newRevision = version.revision + 1
    storeSchema(schemaId, schema, version.copy(revision = newRevision))
  }

  private def storeSchema(schemaId: String, schema: JsValue, version: SchemaVer.Full) = {
    val selfDescribingSchema = SelfDescribingSchema[JsValue](
      SchemaMap(SchemaKey("com.iglu.sample", schemaId, "jsonschema", version)),
      schema
    )
    schemaStore.upload(schemaId, selfDescribingSchema)
  }

  private def insert(schemaId: String, schema: JsValue) = {
    storeSchema(schemaId, schema, SchemaVer.Full(1, 1, 0))
  }

  def readSchema(schemaId: String) = Action {
    schemaStore.read(schemaId) match {
      case Some(schema) =>
        Ok(schema.schema)
      case _ =>
        NotFound
    }
  }

  def validateDocument(schemaId: String) = Action(parse.json) { request =>
    schemaStore.read(schemaId) match {
      case Some(schema) =>
        validate(schemaId, request.body, schema.schema)
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

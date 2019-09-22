package controllers

import com.snowplowanalytics.iglu.core.{SchemaKey, SchemaMap, SchemaVer, SelfDescribingSchema}
import core.SchemaStore
import javax.inject.Inject
import models.ApiModels.Response
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

class SchemaApiController @Inject()(cc: ControllerComponents, schemaStore: SchemaStore[JsValue]) extends AbstractController(cc) {

  def uploadSchema(schemaId: String) = Action(parse.json) { request =>
    val schema = SelfDescribingSchema[JsValue](
      SchemaMap(SchemaKey("com.iglu.sample", schemaId, "jsonschema", SchemaVer.Full(1,0,0))),
      request.body
    )
    schemaStore.upload(schemaId, schema)
    Created(Json.toJson(Response("uploadSchema", schemaId, "success")))
  }

  def readSchema(schemaId: String) = Action {
    schemaStore.read(schemaId) match {
      case Some(schema) =>
        Ok(schema.schema)
      case _ =>
        NotFound
    }
  }

  def validateDocument(schemaId: String) = Action {
    Ok("Success")
  }
}

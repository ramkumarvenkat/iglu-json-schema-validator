package core.validator

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.main.{JsonSchemaFactory, JsonValidator}
import javax.inject.Singleton
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

import scala.collection.JavaConverters._

@Singleton
class JsonSchemaValidator extends SchemaValidator[JsValue, JsValue] {

  lazy val validator = getJsonSchemaValidator(SchemaVersion.DRAFTV4)

  override def validate(data: JsValue, schema: JsValue): Either[List[String], Unit] = {
    val report = validator.validate(getJsonNode(schema), getJsonNode(data))
    report.iterator.asScala.toList match {
      case head :: tl if !report.isSuccess =>
        Left((head :: tl).map(m => extractMessage(m.asJson())))
      case _ if report.isSuccess => Right(())
    }
  }

  private def extractMessage(json: JsonNode): String = {
    Json.toJson(json) match {
      case JsObject(fields) =>
        val jsonObject = fields.toMap
        val pointer = for {
          JsObject(instance) <- jsonObject.get("instance")
          JsString(pointer) <- instance.toMap.get("pointer")
        } yield pointer
        val keyword = jsonObject.get("keyword").flatMap {
          case JsString(kw) => Some(kw)
          case _ => None
        }
        val message = jsonObject.getOrElse("message", JsString("Unknown message from Schema Validator")).as[JsString].value
        s"Error[${keyword.getOrElse("")}] in[${pointer.getOrElse("")}] reason[$message]"
      case _ =>
        "Unknown message from Schema Validator"
    }
  }

  private def getJsonNode(value: JsValue) = {
    JsonLoader.fromString(value.toString())
  }

  private def getJsonSchemaValidator(version: SchemaVersion): JsonValidator = {
    val config = ValidationConfiguration
      .newBuilder
      .setDefaultVersion(version)
      .freeze
    val factory = JsonSchemaFactory
      .newBuilder
      .setValidationConfiguration(config)
      .freeze
    factory.getValidator
  }
}

package core.store

import akka.util.ByteString
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.snowplowanalytics.iglu.core.{SchemaKey, SelfDescribingSchema}
import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import play.api.libs.ws.{BodyWritable, InMemoryBody}

trait SelfDescribingSchemaBodyWritables {

  // Copied and modified from play.api.libs.ws.JsonBodyWritables

  implicit val implicitSchemaKeyWrites = new Writes[SchemaKey] {
    def writes(k: SchemaKey): JsValue = {
      Json.obj("vendor" -> k.vendor, "name" -> k.name, "format" -> k.format, "version" -> k.version.asString)
    }
  }

  implicit val writeableOf_JsValue: BodyWritable[SelfDescribingSchema[JsValue]] =
    BodyWritable(a => {
      val json = a.schema.as[JsObject] + ("self" -> Json.toJson(a.self.schemaKey))
      InMemoryBody(ByteString.fromArrayUnsafe(Json.toBytes(json)))
    }, "application/json")

  def body(objectMapper: ObjectMapper): BodyWritable[JsonNode] = BodyWritable(json =>
    InMemoryBody(ByteString.fromArrayUnsafe(objectMapper.writer.writeValueAsBytes(json))), "application/json")
}

object SelfDescribingSchemaBodyWritables extends SelfDescribingSchemaBodyWritables
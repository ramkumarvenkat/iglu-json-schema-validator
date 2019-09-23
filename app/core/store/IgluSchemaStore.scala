package core.store

import com.snowplowanalytics.iglu.core.{SchemaKey, SchemaMap, SchemaVer, SelfDescribingSchema}
import core.store.SelfDescribingSchemaBodyWritables._
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class IgluSchemaStore @Inject()(config: Configuration, ws: WSClient) extends SchemaStore[JsValue] {

  private val apiUrl = config.get[String]("iglu.url")
  private val apiToken = config.get[String]("iglu.token")
  private val vendor = config.get[String]("iglu.vendor")

  override def upload(schemaId: String, schema: JsValue): Try[JsValue] = {
    val schemaKey = SchemaKey(vendor, schemaId, "jsonschema", SchemaVer.Full(1, 1, 0))
    val selfDescribingSchema = SelfDescribingSchema[JsValue](
      SchemaMap(schemaKey),
      schema
    )

    val request = getURL(schemaKey)

    val response: Future[Try[JsValue]] = request.put(selfDescribingSchema).map {
      case response if response.status == 200 || response.status == 201 =>
        Success(Json.toJson(response.body))
      case response =>
        Failure(new RuntimeException("Iglu server returned error during upload: " + response.body))
    }
    val res = Await.result(response, 5000 millis)
    res
  }

  override def read(schemaId: String): Try[JsValue] = {
    val schemaKey = SchemaKey(vendor, schemaId, "jsonschema", SchemaVer.Full(1, 1, 0))
    val request = getURL(schemaKey)

    val response: Future[Try[JsValue]] = request.get().map {
      case response if response.status == 200 || response.status == 201 =>
        Success(Json.parse(response.body))
      case response =>
        Failure(new RuntimeException("Iglu server returned error during get: " + response.body))
    }

    Await.result(response, 5000 millis)
  }

  private def getURL(schemaKey: SchemaKey): WSRequest = ws.url(
    apiUrl + "/api/schemas/" + schemaKey.vendor + "/" + schemaKey.name + "/" + schemaKey.format + "/" + schemaKey.version.asString
  ).addHttpHeaders("Content-Type" -> "application/json", "apikey" -> apiToken)
}

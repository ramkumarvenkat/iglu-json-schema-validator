package core.store

import java.util.concurrent.ConcurrentHashMap

import javax.inject.Singleton
import play.api.libs.json.JsValue

import scala.util.{Success, Try}

@Singleton
class MemorySchemaStore extends SchemaStore[JsValue] {

  private val schemaStoreMap = new ConcurrentHashMap[String, JsValue]()

  override def upload(schemaId: String, schema: JsValue): Try[JsValue] = {
    Success(schemaStoreMap.put(schemaId, schema))
  }

  override def read(schemaId: String): Try[JsValue] = {
    Try(schemaStoreMap.get())
  }
}

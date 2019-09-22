package core.store

import java.util.concurrent.ConcurrentHashMap

import com.snowplowanalytics.iglu.core.SelfDescribingSchema
import javax.inject.Singleton
import play.api.libs.json.JsValue

@Singleton
class MemorySchemaStore extends SchemaStore[JsValue] {

  private val schemaStoreMap = new ConcurrentHashMap[String, SelfDescribingSchema[JsValue]]()

  override def upload(schemaId: String, schema: SelfDescribingSchema[JsValue]): SelfDescribingSchema[JsValue] = {
    schemaStoreMap.put(schemaId, schema)
  }

  override def read(schemaId: String): Option[SelfDescribingSchema[JsValue]] = {
    Option(schemaStoreMap.get(schemaId))
  }
}

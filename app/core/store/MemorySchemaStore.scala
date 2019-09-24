package core.store

import java.util.concurrent.ConcurrentHashMap

import javax.inject.Singleton
import play.api.libs.json.JsValue

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

@Singleton
class MemorySchemaStore extends SchemaStore[JsValue] {

  private val schemaStoreMap = new ConcurrentHashMap[String, JsValue]().asScala

  override def upload(schemaId: String, schema: JsValue): Try[JsValue] = {
    schemaStoreMap.put(schemaId, schema) match {
      case Some(schema) => Success(schema)
      case _ => Failure(new RuntimeException("Memory schema store returned error during upload"))
    }
  }

  override def read(schemaId: String): Try[JsValue] = {
    schemaStoreMap.get(schemaId) match {
      case Some(schema) => Success(schema)
      case _ => Failure(new RuntimeException("Memory schema store returned error during get"))
    }
  }
}

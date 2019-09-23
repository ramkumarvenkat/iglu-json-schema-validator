package core.store

import com.google.inject.ImplementedBy

import scala.util.Try

@ImplementedBy(classOf[IgluSchemaStore])
trait SchemaStore[T] {
  def upload(schemaId: String, schema: T): Try[T]
  def read(schemaId: String): Try[T]
}

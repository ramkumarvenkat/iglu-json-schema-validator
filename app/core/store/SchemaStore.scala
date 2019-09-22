package core.store

import com.google.inject.ImplementedBy
import com.snowplowanalytics.iglu.core.SelfDescribingSchema

@ImplementedBy(classOf[MemorySchemaStore])
trait SchemaStore[T] {
  def upload(schemaId: String, schema: SelfDescribingSchema[T]): SelfDescribingSchema[T]

  def read(schemaId: String): Option[SelfDescribingSchema[T]]
}

package core.validator

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[JsonSchemaValidator])
trait SchemaValidator[S, D] {
  def validate(data: D, schema: S): Either[List[String], Unit]
}

package core.validator

import helper.BaseSpec
import org.specs2.mutable._

class JsonSchemaValidatorSpec extends Specification with BaseSpec {

  private val validator = new JsonSchemaValidator()

  "JsonSchemaValidator, when the data is valid" should {
    "not return any error for schema1" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val testCase = getAsJson("/api/request/validateDocument-request.json")

      def result = validator.validate(testCase, inputSchema)

      result must beRight
    }

    "not return any error for schema2" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request2.json")
      val testCase = getAsJson("/api/request/validateDocument-request2.json")

      def result = validator.validate(testCase, inputSchema)

      result must beRight
    }
  }

  "JsonSchemaValidator, when the data is not valid" should {
    "return error if data contains null" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val testCase = getAsJson("/api/request/validateDocument-request-nulls.json")

      def result = validator.validate(testCase, inputSchema)

      result must beLeft(List(
        "Error[type] in[/chunks/number] reason[instance type (null) does not match any allowed primitive type (allowed: [\"integer\"])]",
        "Error[type] in[/timeout] reason[instance type (null) does not match any allowed primitive type (allowed: [\"integer\"])]"
      ))

      val inputSchema2 = getAsJson("/api/request/uploadSchema-request2.json")
      val testCase2 = getAsJson("/api/request/validateDocument-request2-nulls.json")

      def result2 = validator.validate(testCase2, inputSchema2)

      result2 must beLeft(List(
        "Error[type] in[/person/name] reason[instance type (null) does not match any allowed primitive type (allowed: [\"string\"])]"
      ))
    }

    "return error if data has null values for array fields" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request2.json")
      val testCase = getAsJson("/api/request/validateDocument-request2-nulls2.json")

      def result = validator.validate(testCase, inputSchema)

      result must beLeft(List(
        "Error[type] in[/person/nicknames] reason[instance type (null) does not match any allowed primitive type (allowed: [\"array\"])]"
      ))
    }

    "return error if data doesn't have required fields" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val testCase = getAsJson("/api/request/validateDocument-request-invalid-data.json")

      def result = validator.validate(testCase, inputSchema)

      result must beLeft(List(
        "Error[required] in[] reason[object has missing required properties ([\"destination\"])]"
      ))
    }

    "return error if data doesn't have required array fields" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request2.json")
      val testCase = getAsJson("/api/request/validateDocument-request2-invalid-data.json")

      def result = validator.validate(testCase, inputSchema)

      result must beLeft(List(
        "Error[required] in[/person/children/0/children/0/children/0] reason[object has missing required properties ([\"nicknames\"])]"
      ))
    }

    "return error if data has wrong data type for a field" in {
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val testCase = getAsJson("/api/request/validateDocument-request-invalid-data2.json")

      def result = validator.validate(testCase, inputSchema)

      result must beLeft(List(
        "Error[type] in[/chunks/number] reason[instance type (string) does not match any allowed primitive type (allowed: [\"integer\"])]"
      ))
    }
  }
}

package controllers

import helper.BaseSpec
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

class SchemaApiControllerSpec extends PlaySpec with BaseSpec with GuiceOneAppPerTest {

  "SchemaApiController create" should {
    "return 201 when the request body is a valid json" in {
      val testCase = getAsJson("/api/request/uploadSchema-request.json")
      val response = getAsJson("/api/response/uploadSchema-response.json")
      val request = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase)

      val schema = route(app, request).get

      status(schema) mustBe CREATED
      contentType(schema) mustBe Some("application/json")
      contentAsJson(schema) mustBe response
    }

    "return 201 when the schema is already uploaded and the new schema is overwritten" in {
      val testCase = getAsJson("/api/request/uploadSchema-request.json")
      val response = getAsJson("/api/response/uploadSchema-response.json")
      val request = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase)

      val schema = route(app, request).get

      status(schema) mustBe CREATED
      contentType(schema) mustBe Some("application/json")
      contentAsJson(schema) mustBe response

      val testCase2 = getAsJson("/api/request/uploadSchema-request2.json")
      val request2 = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase2)

      val schema2 = route(app, request2).get

      status(schema2) mustBe CREATED
      contentType(schema2) mustBe Some("application/json")
      contentAsJson(schema2) mustBe response
    }

    "return 400 when the request body is NOT a valid json" in {
      val testCase = getAsText("/api/request/uploadSchema-request-error.json")
      val response = getAsJson("/api/response/uploadSchema-response-error.json")
      val request = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withTextBody(testCase)

      val schema = route(app, request).get

      status(schema) mustBe BAD_REQUEST
      contentType(schema) mustBe Some("application/json")
      contentAsJson(schema) mustBe response
    }
  }

  "SchemaApiController read" should {
    "return 200 when reading an uploaded schema" in {
      // Upload schema
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val uploadRequest = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(inputSchema)
      route(app, uploadRequest).get

      // Read schema
      val readRequest = FakeRequest(GET, "/schema/config-schema")
      val responseSchema = route(app, readRequest).get

      status(responseSchema) mustBe OK
      contentType(responseSchema) mustBe Some("application/json")
      contentAsJson(responseSchema) mustBe inputSchema
    }

    "return 404 when reading a schema, which is not uploaded" in {
      // Read schema
      val readRequest = FakeRequest(GET, "/schema/not_found")
      val responseSchema = route(app, readRequest).get

      status(responseSchema) mustBe NOT_FOUND
    }
  }

  "SchemaApiController validate" should {
    "return 200 if the schema is uploaded and data is valid and without nulls" in {
      // Upload schema
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val uploadRequest = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(inputSchema)
      route(app, uploadRequest).get

      // Validate document
      val testCase = getAsJson("/api/request/validateDocument-request.json")
      val response = getAsJson("/api/response/validateDocument-response.json")
      val request = FakeRequest(POST, "/validate/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase)

      val validateDocumentResponse = route(app, request).get

      status(validateDocumentResponse) mustBe OK
      contentType(validateDocumentResponse) mustBe Some("application/json")
      contentAsJson(validateDocumentResponse) mustBe response
    }

    "return 200 if the schema is uploaded and data is valid, with nulls and nulls are cleaned up" in {
      // Upload schema
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val uploadRequest = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(inputSchema)
      route(app, uploadRequest).get

      // Upload schema2
      val inputSchema2 = getAsJson("/api/request/uploadSchema-request2.json")
      val uploadRequest2 = FakeRequest(POST, "/schema/family-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(inputSchema2)
      route(app, uploadRequest2).get

      // Validate document
      val testCase = getAsJson("/api/request/validateDocument-request-nulls.json")
      val response = getAsJson("/api/response/validateDocument-response.json")
      val request = FakeRequest(POST, "/validate/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase)

      val validateDocumentResponse = route(app, request).get

      status(validateDocumentResponse) mustBe OK
      contentType(validateDocumentResponse) mustBe Some("application/json")
      contentAsJson(validateDocumentResponse) mustBe response

      // Validate document2
      val testCase2 = getAsJson("/api/request/validateDocument-request2-nulls.json")
      val response2 = getAsJson("/api/response/validateDocument-response2.json")
      val request2 = FakeRequest(POST, "/validate/family-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase2)

      val validateDocumentResponse2 = route(app, request2).get

      status(validateDocumentResponse2) mustBe OK
      contentType(validateDocumentResponse2) mustBe Some("application/json")
      contentAsJson(validateDocumentResponse2) mustBe response2
    }

    "return 400 when the request body is NOT a valid json" in {
      val testCase = getAsText("/api/request/validateDocument-request-error.json")
      val response = getAsJson("/api/response/validateDocument-response-error.json")
      val request = FakeRequest(POST, "/validate/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withTextBody(testCase)

      val schema = route(app, request).get

      status(schema) mustBe BAD_REQUEST
      contentType(schema) mustBe Some("application/json")
      contentAsJson(schema) mustBe response
    }

    "return 404 if the schema is not uploaded" in {
      // Validate document
      val testCase = getAsJson("/api/request/validateDocument-request.json")
      val request = FakeRequest(POST, "/validate/not-found")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase)

      val responseSchema = route(app, request).get

      status(responseSchema) mustBe NOT_FOUND
    }

    "return 400 if the schema is uploaded and data is invalid" in {
      // Upload schema
      val inputSchema = getAsJson("/api/request/uploadSchema-request.json")
      val uploadRequest = FakeRequest(POST, "/schema/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(inputSchema)
      route(app, uploadRequest).get

      // Validate document
      val testCase = getAsJson("/api/request/validateDocument-request-invalid-data.json")
      val response = getAsJson("/api/response/validateDocument-response-invalid-data.json")
      val request = FakeRequest(POST, "/validate/config-schema")
        .withHeaders(CONTENT_TYPE -> "application/json")
        .withJsonBody(testCase)

      val validateDocumentResponse = route(app, request).get

      status(validateDocumentResponse) mustBe BAD_REQUEST
      contentType(validateDocumentResponse) mustBe Some("application/json")
      contentAsJson(validateDocumentResponse) mustBe response
    }
  }
}

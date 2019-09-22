package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

class SchemaApiControllerSpec extends PlaySpec with BaseSpec with GuiceOneAppPerTest {

  "SchemaApiController create" should {
    "return 201 when the request body is a valid json" in {
      val testCase = getAsJson("/api/request/uploadSchema-request.json")
      val response = getAsJson("/api/response/uploadSchema-response.json")
      val request = FakeRequest(POST, "/schema/config-schema" )
        .withHeaders(CONTENT_TYPE ->  "application/json")
        .withJsonBody(testCase)

      val schema = route(app, request).get

      status(schema) mustBe CREATED
      contentType(schema) mustBe Some("application/json")
      contentAsJson(schema) mustBe response
    }

    "return 400 when the request body is NOT a valid json" in {
      val testCase = getAsText("/api/request/uploadSchema-request-error.json")
      val response = getAsJson("/api/response/uploadSchema-response-error.json")
      val request = FakeRequest(POST, "/schema/config-schema" )
        .withHeaders(CONTENT_TYPE ->  "application/json")
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
      val uploadRequest = FakeRequest(POST, "/schema/config-schema" )
        .withHeaders(CONTENT_TYPE ->  "application/json")
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
    "return 200" in {
      val request = FakeRequest(POST, "/validate/id1")
      val schema = route(app, request).get

      status(schema) mustBe OK
      contentType(schema) mustBe Some("text/plain")
      contentAsString(schema) must include ("Success")
    }
  }
}

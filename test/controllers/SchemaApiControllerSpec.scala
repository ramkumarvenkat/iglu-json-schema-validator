package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

class SchemaApiControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "SchemaApiController create" should {
    "return 201" in {
      val request = FakeRequest(POST, "/schema/id1")
      val schema = route(app, request).get

      status(schema) mustBe CREATED
      contentType(schema) mustBe Some("text/plain")
      contentAsString(schema) must include ("Success")
    }
  }

  "SchemaApiController read" should {
    "return 200" in {
      val request = FakeRequest(GET, "/schema/id1")
      val schema = route(app, request).get

      status(schema) mustBe OK
      contentType(schema) mustBe Some("text/plain")
      contentAsString(schema) must include ("Success")
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

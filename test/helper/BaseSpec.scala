package helper

import play.api.libs.json.{JsValue, Json}

import scala.io.Source

trait BaseSpec {

  def getAsJson(file: String): JsValue = {
    val fileContent = getAsText(file)
    Json.parse(fileContent)
  }

  def getAsText(file: String): String = {
    Source.fromURL(getClass.getResource(file)).mkString
  }
}

package models

import play.api.libs.json.Json

object ApiModels {

  case class Response(action: String, id: String, status: String, message: Option[String] = None)

  implicit val implicitResponseWrites = Json.writes[Response]
}

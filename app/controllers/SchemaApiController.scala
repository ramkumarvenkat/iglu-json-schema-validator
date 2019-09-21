package controllers

import javax.inject.Inject
import play.api.mvc._

class SchemaApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create(schemaId: String) = Action {
    Created("Success")
  }

  def read(schemaId: String) = Action {
    Ok("Success")
  }

  def validate(schemaId: String) = Action {
    Ok("Success")
  }
}

import javax.inject.{Inject, Provider, Singleton}
import models.ApiModels.Response
import play.api.http.DefaultHttpErrorHandler
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}
import play.core.SourceMapper

import scala.concurrent._

@Singleton
class ErrorHandler(environment: Environment,
                   configuration: Configuration,
                   sourceMapper: Option[SourceMapper] = None,
                   optionRouter: => Option[Router] = None) extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, optionRouter) {

  @Inject
  def this(environment: Environment,
           configuration: Configuration,
           sourceMapper: OptionalSourceMapper,
           router: Provider[Router]) = {
    this(environment,
      configuration,
      sourceMapper.sourceMapper,
      Some(router.get))
  }

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful {
      val result = statusCode match {
        case BAD_REQUEST =>
          val handler = request.attrs(Router.Attrs.HandlerDef)
          val id = request.path.substring(request.path.lastIndexOf('/') + 1)
          BadRequest(Json.toJson(Response(handler.method, id, "error", Some("Invalid Json"))))
        case _ =>
          Status(statusCode)
      }
      result
    }
  }
}

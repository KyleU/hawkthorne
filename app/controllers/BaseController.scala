package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, SignUpEvent}
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import io.circe.{Json, Printer}
import models.Application
import models.auth.{AuthEnv, Credentials}
import models.result.data.DataField
import models.user.{Role, SystemUser, UserPreferences}
import play.api.http.{ContentTypeOf, Writeable}
import play.api.mvc._
import util.Logging
import util.metrics.Instrumented
import util.tracing.TraceData
import util.web.{ControllerUtils, TracingFilter}

import scala.language.implicitConversions
import scala.concurrent.{ExecutionContext, Future}

abstract class BaseController(val name: String) extends InjectedController with Logging {
  type Req = SecuredRequest[AuthEnv, AnyContent]

  private[this] def cn(x: Any) = x.getClass.getSimpleName.replaceAllLiterally("$", "")

  protected def app: Application

  protected[this] lazy val metricsName = util.Config.metricsId + "_" + cn(this)

  protected def withoutSession(action: String)(block: UserAwareRequest[AuthEnv, AnyContent] => TraceData => Future[Result])(implicit ec: ExecutionContext) = {
    app.silhouette.UserAwareAction.async { implicit request =>
      Instrumented.timeFuture(metricsName + "_request", "action", name + "_" + action) {
        app.tracing.trace(name + ".controller." + action) { td =>
          ControllerUtils.enhanceRequest(request, request.identity, td)
          block(request)(td)
        }(getTraceData)
      }
    }
  }

  protected def withSession(action: String, admin: Boolean = false)(block: Req => TraceData => Future[Result])(implicit ec: ExecutionContext) = {
    app.silhouette.UserAwareAction.async { implicit request =>
      Instrumented.timeFuture(metricsName + "_request", "action", name + "_" + action) {
        app.tracing.trace(name + ".controller." + action) { td =>
          request.identity match {
            case Some(u) => if (admin && u.role != Role.Admin) {
              failRequest(request)
            } else {
              ControllerUtils.enhanceRequest(request, Some(u), td)
              val auth = request.authenticator.getOrElse(throw new IllegalStateException("No auth!"))
              block(SecuredRequest(u, auth, request))(td)
            }
            case None if !admin =>
              val userId = UUID.randomUUID()
              val loginInfo = LoginInfo("anonymous", userId.toString)
              val user = SystemUser(id = userId, username = "Guest " + userId, preferences = UserPreferences(), profile = loginInfo)
              app.coreServices.users.insert(Credentials.system, user)(td).flatMap { u =>
                app.silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                  app.silhouette.env.authenticatorService.init(authenticator).flatMap { value =>
                    block(SecuredRequest[AuthEnv, AnyContent](u, authenticator, request))(td).flatMap { result =>
                      app.silhouette.env.authenticatorService.embed(value, result).map { authedResponse =>
                        ControllerUtils.enhanceRequest(request, Some(u), td)
                        app.silhouette.env.eventBus.publish(SignUpEvent(u, request))
                        app.silhouette.env.eventBus.publish(LoginEvent(u, request))
                        authedResponse
                      }
                    }
                  }
                }
              }
            case None => failRequest(request)
          }
        }(getTraceData)
      }
    }
  }

  protected def getTraceData(implicit requestHeader: RequestHeader) = requestHeader.attrs(TracingFilter.traceKey)

  protected implicit def toCredentials(request: SecuredRequest[AuthEnv, _]): Credentials = Credentials.fromRequest(request)

  private[this] val defaultPrinter = Printer.spaces2
  protected implicit val contentTypeOfJson: ContentTypeOf[Json] = ContentTypeOf(Some("application/json"))

  protected implicit def writableOfJson(implicit codec: Codec, printer: Printer = defaultPrinter): Writeable[Json] = {
    Writeable(a => codec.encode(a.pretty(printer)))
  }

  protected def modelForm(body: AnyContent) = body.asFormUrlEncoded match {
    case Some(x) => ControllerUtils.modelForm(x)
    case None => ControllerUtils.jsonBody(body).as[Seq[DataField]].getOrElse(throw new IllegalStateException("Json must be an array of DataFields."))
  }

  protected def failRequest(request: UserAwareRequest[AuthEnv, AnyContent]) = {
    val msg = request.identity match {
      case Some(_) => "You must be an administrator to access that."
      case None => s"You must sign in or register before accessing ${util.Config.projectName}."
    }
    val res = Redirect(controllers.auth.routes.AuthenticationController.signInForm())
    Future.successful(res.flashing("error" -> msg).withSession(request.session + ("returnUrl" -> request.uri)))
  }
}

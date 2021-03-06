/* Generated File */
package controllers.admin.user

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import controllers.admin.ServiceController
import models.Application
import models.result.RelationCount
import play.api.http.MimeTypes
import services.analytics.AnalyticsActionService
import services.audit.AuditRecordService
import services.history.{GameHistoryService, GamePlayerService}
import services.note.NoteService
import services.trace.TraceResultService
import services.user.SystemUserService
import util.FutureUtils.defaultContext
import util.JsonSerializers._
import util.ReftreeUtils._

@javax.inject.Singleton
class SystemUserController @javax.inject.Inject() (
    override val app: Application, svc: SystemUserService, auditRecordSvc: AuditRecordService, val authInfoRepository: AuthInfoRepository, val hasher: PasswordHasher,
    analyticsActionS: AnalyticsActionService, gameHistoryS: GameHistoryService, gamePlayerS: GamePlayerService, noteS: NoteService, traceResultS: TraceResultService
) extends ServiceController(svc) with UserEditHelper with UserSearchHelper {
  def view(id: java.util.UUID, t: Option[String] = None) = withSession("view", admin = true) { implicit request => implicit td =>
    val modelF = svc.getByPrimaryKey(request, id)
    val notesF = app.coreServices.notes.getFor(request, "systemUser", id)
    val auditsF = auditRecordSvc.getByModel(request, "systemUser", id)

    notesF.flatMap(notes => auditsF.flatMap(audits => modelF.map {
      case Some(model) => renderChoice(t) {
        case MimeTypes.HTML => Ok(views.html.admin.user.systemUserView(request.identity, model, notes, audits, app.config.debug))
        case MimeTypes.JSON => Ok(model.asJson)
        case ServiceController.MimeTypes.png => Ok(renderToPng(v = model)).as(ServiceController.MimeTypes.png)
        case ServiceController.MimeTypes.svg => Ok(renderToSvg(v = model)).as(ServiceController.MimeTypes.svg)
      }
      case None => NotFound(s"No SystemUser found with id [$id].")
    }))
  }

  def relationCounts(id: java.util.UUID) = withSession("relation.counts", admin = true) { implicit request => implicit td =>
    val creds = models.auth.Credentials.fromRequest(request)
    val analyticsActionByAuthorF = analyticsActionS.countByAuthor(creds, id)
    val gameHistoryByCreatorF = gameHistoryS.countByCreator(creds, id)
    val gamePlayerByUserIdF = gamePlayerS.countByUserId(creds, id)
    val noteByAuthorF = noteS.countByAuthor(creds, id)
    val traceResultByAuthorF = traceResultS.countByAuthor(creds, id)
    for (analyticsActionC <- analyticsActionByAuthorF; gameHistoryC <- gameHistoryByCreatorF; gamePlayerC <- gamePlayerByUserIdF; noteC <- noteByAuthorF; traceResultC <- traceResultByAuthorF) yield {
      Ok(Seq(
        RelationCount(model = "analyticsAction", field = "author", count = analyticsActionC),
        RelationCount(model = "gameHistory", field = "creator", count = gameHistoryC),
        RelationCount(model = "gamePlayer", field = "userId", count = gamePlayerC),
        RelationCount(model = "note", field = "author", count = noteC),
        RelationCount(model = "traceResult", field = "author", count = traceResultC)
      ).asJson)
    }
  }
}

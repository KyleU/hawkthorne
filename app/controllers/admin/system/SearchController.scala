package controllers.admin.system

import java.util.UUID

import controllers.BaseController
import models.Application
import models.auth.Credentials
import play.twirl.api.Html
import services.ServiceRegistry
import util.tracing.TraceData

import scala.concurrent.Future

@javax.inject.Singleton
class SearchController @javax.inject.Inject() (override val app: Application, services: ServiceRegistry) extends BaseController("search") {
  import app.contexts.webContext

  def search(q: String) = withSession("admin.search", admin = true) { implicit request => implicit td =>
    val creds = Credentials.fromRequest(request)
    val results = try {
      searchInt(creds, q, q.toInt)
    } catch {
      case _: NumberFormatException => try {
        searchUuid(creds, q, UUID.fromString(q))
      } catch {
        case _: IllegalArgumentException => searchString(creds, q)
      }
    }
    results.map { r =>
      Ok(views.html.admin.explore.searchResults(q, r, request.identity))
    }
  }

  private[this] def searchInt(creds: Credentials, q: String, id: Int)(implicit timing: TraceData) = {
    // Start int searches
    val intSearches = Seq.empty[Future[Seq[Html]]]
    // End int searches

    Future.sequence(intSearches).map(_.flatten)
  }

  private[this] def searchUuid(creds: Credentials, q: String, id: UUID)(implicit timing: TraceData) = {
    // Start uuid searches
    val analyticsAction = services.analyticsServices.analyticsActionService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.analytics.analyticsActionSearchResult(model, s"Analytics Action [${model.id}] matched [$q].")
    }.toSeq)
    val auditRecord = services.auditServices.auditRecordService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.audit.auditRecordSearchResult(model, s"Audit Record [${model.id}] matched [$q].")
    }.toSeq)
    val gameHistory = services.historyServices.gameHistoryService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.history.gameHistorySearchResult(model, s"Game History [${model.id}] matched [$q].")
    }.toSeq)
    val gameSnapshot = services.historyServices.gameSnapshotService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.history.gameSnapshotSearchResult(model, s"Game Snapshot [${model.id}] matched [$q].")
    }.toSeq)
    val note = services.noteServices.noteService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.note.noteSearchResult(model, s"Note [${model.id}] matched [$q].")
    }.toSeq)
    val scheduledTaskRun = services.taskServices.scheduledTaskRunService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.task.scheduledTaskRunSearchResult(model, s"Scheduled Task Run [${model.id}] matched [$q].")
    }.toSeq)
    val systemUser = services.userServices.systemUserService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.user.systemUserSearchResult(model, s"System User [${model.id}] matched [$q].")
    }.toSeq)
    val traceResult = services.traceServices.traceResultService.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.trace.traceResultSearchResult(model, s"Trace Result [${model.id}] matched [$q].")
    }.toSeq)

    val uuidSearches = Seq[Future[Seq[Html]]](analyticsAction, auditRecord, gameHistory, gameSnapshot, note, scheduledTaskRun, systemUser, traceResult)
    // End uuid searches

    val auditR = app.coreServices.audits.getByPrimaryKey(creds, id).map(_.map { model =>
      views.html.admin.audit.auditSearchResult(model, s"Audit [${model.id}] matched [$q].")
    }.toSeq)

    Future.sequence(auditR +: uuidSearches).map(_.flatten)
  }

  private[this] def searchString(creds: Credentials, q: String)(implicit timing: TraceData) = {
    // Start string searches
    val analyticsAction = services.analyticsServices.analyticsActionService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.analytics.analyticsActionSearchResult(model, s"Analytics Action [${model.id}] matched [$q].")
    })
    val auditRecord = services.auditServices.auditRecordService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.audit.auditRecordSearchResult(model, s"Audit Record [${model.id}] matched [$q].")
    })
    val gameHistory = services.historyServices.gameHistoryService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.history.gameHistorySearchResult(model, s"Game History [${model.id}] matched [$q].")
    })
    val gamePlayer = services.historyServices.gamePlayerService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.history.gamePlayerSearchResult(model, s"Game Player [${model.gameId}, ${model.idx}] matched [$q].")
    })
    val gameSnapshot = services.historyServices.gameSnapshotService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.history.gameSnapshotSearchResult(model, s"Game Snapshot [${model.id}] matched [$q].")
    })
    val note = services.noteServices.noteService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.note.noteSearchResult(model, s"Note [${model.id}] matched [$q].")
    })
    val scheduledTaskRun = services.taskServices.scheduledTaskRunService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.task.scheduledTaskRunSearchResult(model, s"Scheduled Task Run [${model.id}] matched [$q].")
    })
    val schemaMigration = services.ddlServices.schemaMigrationService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.ddl.schemaMigrationSearchResult(model, s"Schema Migration [${model.installedRank}] matched [$q].")
    })
    val syncProgress = services.syncServices.syncProgressService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.sync.syncProgressSearchResult(model, s"Sync Progress [${model.key}] matched [$q].")
    })
    val systemUser = services.userServices.systemUserService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.user.systemUserSearchResult(model, s"System User [${model.id}] matched [$q].")
    })
    val traceResult = services.traceServices.traceResultService.searchExact(creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.trace.traceResultSearchResult(model, s"Trace Result [${model.id}] matched [$q].")
    })

    val stringSearches = Seq[Future[Seq[Html]]](analyticsAction, auditRecord, schemaMigration, gameHistory, gamePlayer, gameSnapshot, note, scheduledTaskRun, syncProgress, systemUser, traceResult)
    // End string searches

    val auditR = app.coreServices.audits.searchExact(creds = creds, q = q, limit = Some(5)).map(_.map { model =>
      views.html.admin.audit.auditSearchResult(model, s"Audit [${model.id}] matched [$q].")
    })

    Future.sequence(auditR +: stringSearches).map(_.flatten)
  }
}

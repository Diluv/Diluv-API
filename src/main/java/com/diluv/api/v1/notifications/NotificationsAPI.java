package com.diluv.api.v1.notifications;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.diluv.api.data.DataNotification;
import com.diluv.api.data.DataNotificationList;
import com.diluv.api.data.DataNotificationProjectInvite;
import com.diluv.api.utils.auth.tokens.Token;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.utils.query.NotificationProjectInviteQuery;
import com.diluv.api.utils.query.NotificationQuery;
import com.diluv.api.utils.response.ResponseUtil;
import com.diluv.api.utils.validator.RequireToken;
import com.diluv.confluencia.Confluencia;
import com.diluv.confluencia.database.record.NotificationProjectInvitePermissionsEntity;
import com.diluv.confluencia.database.record.NotificationProjectInvitesEntity;
import com.diluv.confluencia.database.record.NotificationProjectInvitesStatus;
import com.diluv.confluencia.database.record.NotificationType;
import com.diluv.confluencia.database.record.NotificationsEntity;
import com.diluv.confluencia.database.record.ProjectAuthorsEntity;
import com.diluv.confluencia.database.record.ProjectsEntity;
import com.diluv.confluencia.database.record.UsersEntity;
import com.diluv.confluencia.database.sort.NotificationSort;
import com.diluv.confluencia.database.sort.Sort;

@ApplicationScoped
@GZIP
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
public class NotificationsAPI {

    @GET
    @Path("/")
    public Response getNotifications (@RequireToken @HeaderParam("Authorization") Token token, @Query NotificationQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        NotificationType type = query.getType();
        Boolean isRead = query.getRead();

        Sort sort = query.getSort(NotificationSort.NEW);

        return Confluencia.getTransaction(session -> {
            final List<NotificationsEntity> notificationsEntities = Confluencia.NOTIFICATION.findAllByUserId(session, page, limit, sort, token.getUserId(), type, isRead);
            final long notificationsCount = Confluencia.NOTIFICATION.countByUserId(session, token.getUserId(), type, isRead);
            final List<DataNotification> notifications = new ArrayList<>();
            for (NotificationsEntity notification : notificationsEntities) {
                notifications.add(new DataNotification(notification));
            }
            return ResponseUtil.successResponse(new DataNotificationList(notifications, notificationsCount));
        });
    }

    @GET
    @Path("/{id}")
    public Response getNotificationById (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("id") long id) {

        return Confluencia.getTransaction(session -> {
            final NotificationsEntity notification = Confluencia.NOTIFICATION.findOneById(session, id);
            if (notification == null || notification.getUser().getId() != token.getUserId()) {
                return ErrorMessage.NOT_FOUND_NOTIFICATION.respond();
            }
            if (notification instanceof NotificationProjectInvitesEntity) {
                return ResponseUtil.successResponse(new DataNotificationProjectInvite((NotificationProjectInvitesEntity) notification));
            }
            return ResponseUtil.successResponse(new DataNotification(notification));
        });
    }

    @PATCH
    @Path("/{id}")
    public Response patchNotificationById (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("id") long id) {

        return Confluencia.getTransaction(session -> {
            final NotificationsEntity notification = Confluencia.NOTIFICATION.findOneById(session, id);
            if (notification.getUser().getId() != token.getUserId()) {
                return ErrorMessage.NOT_FOUND_NOTIFICATION.respond();
            }
            if (notification.getViewedAt() == null) {
                notification.setViewedAt(Instant.now());
                session.update(notification);
            }
            return ResponseUtil.noContent();
        });
    }

    @GET
    @Path("/project/invite")
    public Response getInvites (@RequireToken @HeaderParam("Authorization") Token token, @Query NotificationProjectInviteQuery query) {

        long page = query.getPage();
        int limit = query.getLimit();
        NotificationProjectInvitesStatus status = query.getStatus();

        Sort sort = query.getSort(NotificationSort.NEW);

        return Confluencia.getTransaction(session -> {
            final List<NotificationProjectInvitesEntity> notificationsEntities = Confluencia.NOTIFICATION.findAllInvitesBySenderOrReceiver(session, page, limit, sort, token.getUserId(), status);
            final long notificationsCount = Confluencia.NOTIFICATION.countInvitesBySenderOrReceiver(session, token.getUserId(), status);
            final List<DataNotification> notifications = new ArrayList<>();
            for (NotificationProjectInvitesEntity notification : notificationsEntities) {
                notifications.add(new DataNotificationProjectInvite(notification));
            }
            return ResponseUtil.successResponse(new DataNotificationList(notifications, notificationsCount));
        });
    }

    @PATCH
    @Path("/project/invite/{id}")
    public Response patchInvite (@RequireToken @HeaderParam("Authorization") Token token, @PathParam("id") long id, @Valid @MultipartForm ProjectInviteForm form) {

        if (form.data == null) {
            return ErrorMessage.INVALID_DATA.respond();
        }
        ProjectInvite data = form.data;

        return Confluencia.getTransaction(session -> {
            final NotificationsEntity notificationEntity = Confluencia.NOTIFICATION.findOneById(session, id);

            if (!(notificationEntity instanceof NotificationProjectInvitesEntity)) {
                return ErrorMessage.NOT_FOUND_INVITE.respond();
            }

            NotificationProjectInvitesEntity projectInvite = (NotificationProjectInvitesEntity) notificationEntity;

            boolean isSender = projectInvite.getSender().getId() == token.getUserId();
            boolean isRecipient = projectInvite.getUser().getId() == token.getUserId();
            if (!isSender && !isRecipient) {
                return ErrorMessage.NOT_FOUND_INVITE.respond();
            }

            if (projectInvite.getStatus() != NotificationProjectInvitesStatus.PENDING) {
                return ErrorMessage.INVALID_PROJECT_INVITE_STATUS.respond("Status is not PENDING.");
            }

            if (isSender) {
                if (data.status != NotificationProjectInvitesStatus.CANCELLED) {
                    return ErrorMessage.INVALID_PROJECT_INVITE_STATUS.respond("Status must be CANCELLED.");
                }
            }
            else if (data.status != NotificationProjectInvitesStatus.ACCEPTED && data.status != NotificationProjectInvitesStatus.DECLINED) {
                return ErrorMessage.INVALID_PROJECT_INVITE_STATUS.respond("Status must be either ACCEPTED or DECLINED.");
            }

            if (data.status == NotificationProjectInvitesStatus.ACCEPTED) {
                ProjectsEntity project = projectInvite.getProject();
                UsersEntity user = projectInvite.getUser();
                String role = projectInvite.getRole();
                ProjectAuthorsEntity projectAuthorEntity = new ProjectAuthorsEntity(user, role);
                if (projectInvite.getPermissions() != null) {
                    for (NotificationProjectInvitePermissionsEntity invitePermissionsEntity : projectInvite.getPermissions()) {
                        projectAuthorEntity.addPermission(invitePermissionsEntity.getPermission());
                    }
                }
                project.addAuthor(projectAuthorEntity);
                session.update(project);
            }

            if (projectInvite.getViewedAt() == null) {
                projectInvite.setViewedAt(Instant.now());
            }

            projectInvite.setStatus(data.status);
            session.update(projectInvite);
            return ResponseUtil.noContent();
        });
    }
}

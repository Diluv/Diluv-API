package com.diluv.api.endpoints.v1;

import com.diluv.api.utils.Request;
import com.diluv.api.utils.TestUtil;
import com.diluv.api.utils.error.ErrorMessage;
import com.diluv.api.v1.notifications.ProjectInvite;
import com.diluv.confluencia.database.record.NotificationProjectInvitesStatus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class NotificationTest {

    private static final String URL = "/v1/notifications";

    @BeforeAll
    public static void setup () {

        TestUtil.start();
    }

    @Test
    public void getNotifications () {

        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL, "schema/notification-list-schema.json");
    }

    @Test
    public void getNotificationById () {

        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/1", ErrorMessage.USER_INVALID_TOKEN);
        Request.getErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/1", ErrorMessage.NOT_FOUND_NOTIFICATION);
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1", "schema/notification-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/6", "schema/notification-schema.json");
        Request.getErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/20", ErrorMessage.NOT_FOUND_NOTIFICATION);
    }

    @Test
    public void patchNotificationById () {

        Request.patchErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/1", null, ErrorMessage.USER_INVALID_TOKEN);
        Request.patchErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/1", null, ErrorMessage.NOT_FOUND_NOTIFICATION);
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/1", null);

    }

    @Test
    public void getInvites () {

        Request.getErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/project/invite", ErrorMessage.USER_INVALID_TOKEN);
        Request.getOkWithAuth(TestUtil.TOKEN_JARED, URL + "/project/invite", "schema/notificationProjectInvite-list-schema.json");
        Request.getOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/project/invite", "schema/notificationProjectInvite-list-schema.json");
    }

    @Test
    public void patchInvite () {

        Map<String, Object> multiPart = new HashMap<>();
        ProjectInvite projectInvite = new ProjectInvite();
        multiPart.put("data", projectInvite);

        Request.patchErrorWithAuth(TestUtil.TOKEN_INVALID, URL + "/project/invite/1", multiPart, ErrorMessage.USER_INVALID_TOKEN);
        Request.patchErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/project/invite/10", multiPart, ErrorMessage.INVALID_PROJECT_INVITE_STATUS);

        projectInvite.status = NotificationProjectInvitesStatus.ACCEPTED;
        Request.patchErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/project/invite/10", multiPart, ErrorMessage.NOT_FOUND_INVITE);
        projectInvite.status = NotificationProjectInvitesStatus.PENDING;
        Request.patchErrorWithAuth(TestUtil.TOKEN_JARED, URL + "/project/invite/6", multiPart, ErrorMessage.INVALID_PROJECT_INVITE_STATUS, "Status must be either ACCEPTED or DECLINED.");
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/project/invite/6", multiPart, ErrorMessage.INVALID_PROJECT_INVITE_STATUS, "Status must be CANCELLED.");
        Request.patchErrorWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/project/invite/7", multiPart, ErrorMessage.INVALID_PROJECT_INVITE_STATUS, "Status is not PENDING.");

        projectInvite.status = NotificationProjectInvitesStatus.CANCELLED;
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/project/invite/6", multiPart);

        projectInvite.status = NotificationProjectInvitesStatus.ACCEPTED;
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/project/invite/2", multiPart);

        projectInvite.status = NotificationProjectInvitesStatus.DECLINED;
        Request.patchOkWithAuth(TestUtil.TOKEN_DARKHAX, URL + "/project/invite/3", multiPart);
    }
}
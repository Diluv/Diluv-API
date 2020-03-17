package com.diluv.api.utils.email;

import java.io.IOException;

import com.diluv.api.DiluvAPIServer;
import com.diluv.api.utils.Constants;
import com.wildbit.java.postmark.Postmark;
import com.wildbit.java.postmark.client.ApiClient;
import com.wildbit.java.postmark.client.data.model.message.Message;
import com.wildbit.java.postmark.client.data.model.message.MessageResponse;
import com.wildbit.java.postmark.client.exception.PostmarkException;

public class EmailUtil {
    public static MessageResponse sendVerificationEmail (String email, String verificationCode) {

        try {
            final ApiClient client = Postmark.getApiClient(Constants.POSTMARK_API_TOKEN);
            final String url = String.format("%s/validate?code=%s&email=%s", Constants.WEBSITE_URL, verificationCode, email);
            if (Constants.EMAIL_VERIFICATION == null) {
                DiluvAPIServer.LOGGER.error("Failed to load email_verification.html.");
                return null;
            }
            final Message message = new Message(Constants.NOREPLY_EMAIL, email, "Diluv Email Verification", Constants.EMAIL_VERIFICATION.replace("{{action_url}}", url));
            return client.deliverMessage(message);
        }
        catch (IOException | PostmarkException e) {

            DiluvAPIServer.LOGGER.error("Failed to send email.", e);
        }
        return null;
    }

    public static MessageResponse sendPasswordReset (String email, String code) {
        try {
            final ApiClient client = Postmark.getApiClient(Constants.POSTMARK_API_TOKEN);
            final String url = String.format("%s/password-reset?code=%s&email=%s", Constants.WEBSITE_URL, code, email);
            if (Constants.PASSWORD_RESET == null) {
                DiluvAPIServer.LOGGER.error("Failed to load password_reset.html.");
                return null;
            }
            final Message message = new Message(Constants.NOREPLY_EMAIL, email, "Diluv Email Password Reset", Constants.PASSWORD_RESET.replace("{{action_url}}", url));
            return client.deliverMessage(message);
        }
        catch (IOException | PostmarkException e) {

            DiluvAPIServer.LOGGER.error("Failed to send email.", e);
        }
        return null;
    }
}

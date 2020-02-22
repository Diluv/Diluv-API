package com.diluv.api.utils.email;

import java.io.IOException;

import com.diluv.api.DiluvAPI;
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
                DiluvAPI.LOGGER.error("Failed to load email_verification.html.");
                return null;
            }
            final Message message = new Message(Constants.NOREPLY_EMAIL, email, "Diluv Email Verification", Constants.EMAIL_VERIFICATION.replace("{{action_url}}", url));
            return client.deliverMessage(message);
        }
        catch (IOException | PostmarkException e) {
            
            DiluvAPI.LOGGER.error("Failed to send email.", e);
        }
        return null;
    }
}

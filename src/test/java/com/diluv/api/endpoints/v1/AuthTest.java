//package com.diluv.api.endpoints.v1;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import com.diluv.api.utils.Request;
//import com.diluv.api.utils.TestUtil;
//import com.diluv.api.utils.error.ErrorMessage;
//
//public class AuthTest {
//
//    private static final String URL = "/v1/auth";
//
//    @BeforeAll
//    public static void setup () {
//
//        TestUtil.start();
//    }
//
//    @Test
//    public void checkUsername () {
//
//        Request.getError(URL + "/checkusername/lclc98", 400, ErrorMessage.USER_TAKEN_USERNAME);
//        Request.getError(URL + "/checkusername/darkhax", 400, ErrorMessage.USER_TAKEN_USERNAME);
//
//        Request.getOk(URL + "/checkusername/nonexisting", null);
//    }
//
//    @Test
//    public void resend () {
//
//        Map<String, Object> multiPart = new HashMap<>();
//        multiPart.put("username", "jaredlll08");
//        multiPart.put("email", "jaredlll08@diluv.com");
//        Request.postError(URL + "/resend", multiPart, 400, ErrorMessage.NOT_FOUND_USER);
//
//        multiPart.put("username", "tempuser2");
//        multiPart.put("email", "tempuser2@diluv.com");
//        Request.postOk(URL + "/resend", multiPart, null);
//    }
//
//    @Test
//    public void requestResetPassword () {
//
//        Map<String, Object> multiPart = new HashMap<>();
//        multiPart.put("username", "jaredlll08");
//        multiPart.put("email", "jaredlll08@diluv.com");
//        Request.postOk(URL + "/request-reset-password", multiPart, null);
//    }
//
//    @Test
//    public void resetPassword () {
//
//        Map<String, Object> multiPart = new HashMap<>();
//        multiPart.put("email", "jaredlll08@diluv.com");
//        multiPart.put("password", "password");
//        multiPart.put("code", "invalid");
//        Request.postError(URL + "/reset-password", multiPart, 400, ErrorMessage.NOT_FOUND_PASSWORD_RESET);
//
//        multiPart.put("code", "daf1f148-effd-400e-9b65-a4bf96e5215d");
//        Request.postError(URL + "/reset-password", multiPart, 400, ErrorMessage.USER_COMPROMISED_PASSWORD);
//
//        multiPart.put("password", "qjeghExvF4SA6HuJLWjM");
//        Request.postOk(URL + "/reset-password", multiPart, null);
//    }
//}
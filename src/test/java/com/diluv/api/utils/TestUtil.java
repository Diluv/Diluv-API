package com.diluv.api.utils;

import com.diluv.api.DiluvAPIServer;
import com.diluv.confluencia.Confluencia;
import io.restassured.RestAssured;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestUtil {

    static final MariaDBContainer CONTAINER;

    static {
        CONTAINER = new MariaDBContainer<>();
        CONTAINER.start();
    }

    public static final String IP = "0.0.0.0";
    public static final int PORT = 4545;
    public static boolean running = false;

    public static String VALID_TOKEN;
    public static String VALID_TOKEN_TWO;
    public static String VALID_LONG_LASTING_TOKEN;
    public static String VALID_REFRESH_TOKEN;
    public static String INVALID_TOKEN = "invalid";

//    static {
//        final Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, 30);
//        try {
//            VALID_TOKEN = new AccessToken(1, "darkhax", Collections.emptyList()).generate(calendar.getTime());
//        }
//        catch (JOSEException e) {
//            e.printStackTrace();
//        }

//        try {
//            VALID_TOKEN_TWO = new AccessToken(2, "jaredlll08", Collections.emptyList()).generate(calendar.getTime());
//        }
//        catch (JOSEException e) {
//            e.printStackTrace();
//        }

//        try {
//            VALID_REFRESH_TOKEN = new RefreshToken(1, "darkhax", "9bd63558-3835-4e01-963f-66a0f467291c").generate(calendar.getTime());
//}
//        catch (JOSEException e) {
//            e.printStackTrace();
//        }
//
//        calendar.add(Calendar.MONTH, 6);

//        try {
//            VALID_LONG_LASTING_TOKEN = new APIAccessToken(1, "darkhax", "4b3b85e3-f7ac-4c7b-b71a-df972909b213", Collections.singletonList(ProjectPermissions.FILE_UPLOAD.getName())).generate();
//        }
//        catch (JOSEException e) {
//            e.printStackTrace();
//        }
//    }

    public static void start () {

        if (!running) {
            Confluencia.init(TestUtil.CONTAINER.getJdbcUrl(), TestUtil.CONTAINER.getUsername(), TestUtil.CONTAINER.getPassword(), true);
            DiluvAPIServer server = new DiluvAPIServer();
            server.start(IP, PORT);

            RestAssured.port = PORT;
            running = true;
        }
    }
}

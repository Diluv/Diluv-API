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

    public static String VALID_TOKEN = "C_RxaYIzXLYxKXD8itdhMUB6J6rKLcvPWpinbpH0ezc";
    public static String VALID_TOKEN_TWO = "r_lN-_POwYxBjf5J2O9Jn889jcpG1QumCs4K3BJ6rFs";
//    public static String VALID_LONG_LASTING_TOKEN = "";
    public static String INVALID_TOKEN = "invalid";

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

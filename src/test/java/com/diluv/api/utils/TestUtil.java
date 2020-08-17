package com.diluv.api.utils;

import com.diluv.api.DiluvAPIServer;
import com.diluv.confluencia.Confluencia;
import io.restassured.RestAssured;

import org.junit.jupiter.api.Assertions;
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

    public static String TOKEN_DARKHAX = "C_RxaYIzXLYxKXD8itdhMUB6J6rKLcvPWpinbpH0ezc";
    public static String TOKEN_JARED = "r_lN-_POwYxBjf5J2O9Jn889jcpG1QumCs4K3BJ6rFs";
    public static String TOKEN_INVALID = "invalid";

    public static void start () {

        if (!running) {
            try {
                Confluencia.init(CONTAINER.getJdbcUrl(), CONTAINER.getUsername(), CONTAINER.getPassword());
                DiluvAPIServer server = new DiluvAPIServer();
                server.start(IP, PORT);

                RestAssured.port = PORT;
                running = true;
            }
            catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Failed to connect to database", e);
            }
        }
    }
}

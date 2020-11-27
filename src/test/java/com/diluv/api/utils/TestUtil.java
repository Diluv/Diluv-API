package com.diluv.api.utils;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.diluv.api.DiluvAPIServer;
import com.diluv.confluencia.Confluencia;
import io.restassured.RestAssured;

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

    public static String TOKEN_DARKHAX = "8dfa96ba-44b0-4eb0-930a-fca183e73ec8";
    public static String TOKEN_JARED = "f0f9c173-2092-48b8-b9de-79242bd684a9";
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

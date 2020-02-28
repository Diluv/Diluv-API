package com.diluv.api;

import com.diluv.api.utils.Constants;

public class Main {

    public static Database DATABASE = new Database();
    public static DiluvAPIServer SERVER = new DiluvAPIServer();

    public static void main (String[] args) {

        DATABASE.init(Constants.DB_HOSTNAME, Constants.DB_USERNAME, Constants.DB_PASSWORD, true);
        SERVER.start("0.0.0.0", 4567);
    }
}
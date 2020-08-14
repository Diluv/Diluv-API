package com.diluv.api;

import com.diluv.api.utils.Constants;
import com.diluv.confluencia.Confluencia;

public class Main {

    public static DiluvAPIServer SERVER = new DiluvAPIServer();

    /**
     * The method to start the API.
     *
     * @param args None
     */
    public static void main (String[] args) {

        Confluencia.init(Constants.DB_HOSTNAME, Constants.DB_USERNAME, Constants.DB_PASSWORD);
        SERVER.start("0.0.0.0", 4567);
    }
}
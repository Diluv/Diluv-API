package com.diluv.api.utils;

public class Salt {
    private String data;
    private int week;

    public Salt (String data, int week) {

        this.data = data;
        this.week = week;
    }

    public String getData () {

        return this.data;
    }

    public void setData (String data) {

        this.data = data;
    }

    public int getWeek () {

        return this.week;
    }

    public void setWeek (int week) {

        this.week = week;
    }
}

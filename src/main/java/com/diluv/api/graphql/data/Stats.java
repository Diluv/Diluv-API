package com.diluv.api.graphql.data;

public class Stats {

    private long gameCount;
    private long projectCount;
    private long unreleasedProjectCount;
    private long userCount;
    private long tempUserCount;
    private long fileSize;

    public Stats (long gameCount, long projectCount, long unreleasedProjectCount, long userCount, long tempUserCount, long fileSize) {

        this.gameCount = gameCount;
        this.projectCount = projectCount;
        this.unreleasedProjectCount = unreleasedProjectCount;
        this.userCount = userCount;
        this.tempUserCount = tempUserCount;
        this.fileSize = fileSize;
    }
}

package com.migapro.jiraclient.models;

import com.google.gson.annotations.SerializedName;

public class Worklog {

    @SerializedName("timeSpent")
    private String timeSpent;

    @SerializedName("comment")
    private String comment;

    @SerializedName("started")
    private String started;


    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }
}

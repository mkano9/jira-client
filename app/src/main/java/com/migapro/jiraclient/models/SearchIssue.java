package com.migapro.jiraclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchIssue {

    @SerializedName("jql")
    private String jql;

    @SerializedName("startAt")
    private int startAt;

    @SerializedName("maxResults")
    private int maxResults;

    @SerializedName("fields")
    private ArrayList<String> fields;

    public String getJql() {
        return jql;
    }

    public void setJql(String jql) {
        this.jql = jql;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }
}

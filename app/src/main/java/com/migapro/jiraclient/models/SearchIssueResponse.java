package com.migapro.jiraclient.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchIssueResponse {

    @SerializedName("issues")
    private ArrayList<Issue> issues;

    public ArrayList<Issue> getIssues() {
        return issues;
    }

    public void setIssues(ArrayList<Issue> issues) {
        this.issues = issues;
    }
}

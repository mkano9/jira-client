package com.migapro.jiraclient.models;

import com.google.gson.annotations.SerializedName;

public class Fields {

    @SerializedName("summary")
    private String summary;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

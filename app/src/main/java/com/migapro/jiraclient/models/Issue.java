package com.migapro.jiraclient.models;

import com.google.gson.annotations.SerializedName;

public class Issue {

    @SerializedName("key")
    private String key;

    @SerializedName("fields")
    private Fields fields;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }
}

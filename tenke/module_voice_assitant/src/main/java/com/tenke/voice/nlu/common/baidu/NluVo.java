
package com.tenke.voice.nlu.common.baidu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class NluVo {

    @Nullable
    @SerializedName("domain")
    private String domain;

    @Nullable
    @SerializedName("intent")
    private String intent;

    @Nullable
    @SerializedName("parser")
    private String parser;

    @SerializedName("score")
    private float score;

    @Nullable
    @SerializedName("object")
    private HashMap hashMap;

    @NonNull
    public String getDomain() {
        return Strings.nullToEmpty(domain);
    }

    public void setDomain(@Nullable String domain) {
        this.domain = domain;
    }

    @NonNull
    public String getIntent() {
        return Strings.nullToEmpty(intent);
    }

    public void setIntent(@Nullable String intent) {
        this.intent = intent;
    }

    @NonNull
    public String getParser() {
        return Strings.nullToEmpty(parser);
    }

    public void setParser(@Nullable String parser) {
        this.parser = parser;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @NonNull
    public HashMap getHashMap() {
        return MoreObjects.firstNonNull(hashMap, new HashMap());
    }

    public void setHashMap(@Nullable HashMap hashMap) {
        this.hashMap = hashMap;
    }
}

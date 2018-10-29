package com.jesussoto.android.popularmovies.api.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class UserReview {

    @Nullable
    @SerializedName("id")
    private String id;

    @Nullable
    @SerializedName("author")
    private String author;

    @Nullable
    @SerializedName("content")
    private String content;

    @Nullable
    @SerializedName("url")
    private String url;

    public UserReview() {

    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    @Nullable
    public String getUrl() {
        return url;
    }
}

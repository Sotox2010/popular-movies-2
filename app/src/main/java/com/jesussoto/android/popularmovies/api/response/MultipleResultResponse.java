package com.jesussoto.android.popularmovies.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MultipleResultResponse<T> {

    @SerializedName("page")
    private Integer id;

    @SerializedName("results")
    private List<T> results;

    public Integer getId() {
        return id;
    }

    public List<T> getResults() {
        return results;
    }
}

package com.jesussoto.android.popularmovies.api.response;

import com.google.gson.annotations.SerializedName;
import com.jesussoto.android.popularmovies.db.entity.Movie;

import java.util.List;

public class PagedResultResponse<T> {

    @SerializedName("page")
    private Integer page;

    @SerializedName("total_results")
    private Integer totalResults;

    @SerializedName("total_pages")
    private Integer totalPages;

    @SerializedName("results")
    private List<T> results;

    public Integer getPage() {
        return page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public List<T> getResults() {
        return results;
    }

}

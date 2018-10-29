package com.jesussoto.android.popularmovies.moviedetail;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.jesussoto.android.popularmovies.api.model.Video;

import java.util.List;

public class MovieVideosUiModel {

    @Nullable
    private List<Video> videos;

    private boolean isProgressVisible;

    private boolean isRetryVisible;

    private boolean isEmptyVisible;

    @Nullable
    @StringRes
    private Integer emptyMessageResId;

    MovieVideosUiModel(
            @Nullable  List<Video> videos, boolean isProgressVisible, boolean isRetryVisible,
            boolean isEmptyVisible, @Nullable @StringRes  Integer emptyMessageResId) {

        this.videos = videos;
        this.isProgressVisible = isProgressVisible;
        this.isRetryVisible = isRetryVisible;
        this.isEmptyVisible = isEmptyVisible;
        this.emptyMessageResId = emptyMessageResId;
    }

    @Nullable
    public List<Video> getVideos() {
        return videos;
    }

    public boolean isProgressVisible() {
        return isProgressVisible;
    }

    public boolean isRetryVisible() {
        return isRetryVisible;
    }

    public boolean isEmptyVisible() {
        return isEmptyVisible;
    }

    @Nullable
    public Integer getEmptyMessageResId() {
        return emptyMessageResId;
    }
}

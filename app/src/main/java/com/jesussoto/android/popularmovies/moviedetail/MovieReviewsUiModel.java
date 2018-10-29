package com.jesussoto.android.popularmovies.moviedetail;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.jesussoto.android.popularmovies.api.model.UserReview;

import java.util.List;

public class MovieReviewsUiModel {

    @Nullable
    private List<UserReview> reviews;

    private boolean isProgressVisible;

    private boolean isRetryVisible;

    private boolean isEmptyVisible;

    @Nullable
    @StringRes
    private Integer emptyMessageResId;

    MovieReviewsUiModel(
            @Nullable  List<UserReview> reviews, boolean isProgressVisible, boolean isRetryVisible,
            boolean isEmptyVisible, @Nullable @StringRes  Integer errorMessageResId) {

        this.reviews = reviews;
        this.isProgressVisible = isProgressVisible;
        this.isRetryVisible = isRetryVisible;
        this.isEmptyVisible = isEmptyVisible;
        this.emptyMessageResId = errorMessageResId;
    }

    @Nullable
    public List<UserReview> getReviews() {
        return reviews;
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

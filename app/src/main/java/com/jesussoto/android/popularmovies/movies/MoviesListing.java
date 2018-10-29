package com.jesussoto.android.popularmovies.movies;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.db.entity.Movie;

import io.reactivex.functions.Action;

/**
 * Wrapper class that encapsulates all the data related to a paged stream of movies, like the movies
 * stream itself, the network state, and retry action.
 */
public class MoviesListing {

    @NonNull
    private LiveData<PagedList<Movie>> mPagedList;

    @Nullable
    private LiveData<Resource.Status> mNetworkState;

    @Nullable
    private LiveData<Resource.Status> mInitialLoadState;

    @Nullable
    private Action mRetryAction;

    public MoviesListing(@NonNull LiveData<PagedList<Movie>> pagedList,
                  @Nullable LiveData<Resource.Status> networkState,
                  @Nullable LiveData<Resource.Status> initialLoadState,
                  @Nullable Action retry) {

        mPagedList = pagedList;
        mNetworkState = networkState;
        mInitialLoadState = initialLoadState;
        mRetryAction = retry;
    }

    @NonNull
    public LiveData<PagedList<Movie>> getPagedList() {
        return mPagedList;
    }

    @Nullable
    public LiveData<Resource.Status> getNetworkState() {
        return mNetworkState;
    }

    @Nullable
    public LiveData<Resource.Status> getInitialLoadState() {
        return mInitialLoadState;
    }

    @Nullable
    public Action getRetryAction() {
        return mRetryAction;
    }

}

package com.jesussoto.android.popularmovies.repository;

import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.api.response.PagedResultResponse;
import com.jesussoto.android.popularmovies.db.entity.Movie;

import retrofit2.Call;

/**
 * Paged data source for Top-rated Movies.
 */
public class TopRatedMoviesPageKeyedDataSource extends AbstractMoviesPageKeyedDataSource {

    public TopRatedMoviesPageKeyedDataSource(@NonNull WebService service) {
        super(service);
    }

    @Override
    Call<PagedResultResponse<Movie>> getCall(@NonNull WebService service, int page) {
        return service.getTopRatedMoviesCall(page);
    }
}

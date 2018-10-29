package com.jesussoto.android.popularmovies.repository;

import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.api.response.PagedResultResponse;
import com.jesussoto.android.popularmovies.db.entity.Movie;

import retrofit2.Call;

/**
 * Paged data source for Popular Movies.
 */
public class PopularMoviesPageKeyedDataSource extends AbstractMoviesPageKeyedDataSource {

    public PopularMoviesPageKeyedDataSource(@NonNull WebService service) {
        super(service);
    }

    @Override
    Call<PagedResultResponse<Movie>> getCall(@NonNull WebService service, int page) {
        return service.getPopularMoviesCall(page);
    }
}

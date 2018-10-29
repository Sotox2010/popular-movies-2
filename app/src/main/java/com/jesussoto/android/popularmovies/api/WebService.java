package com.jesussoto.android.popularmovies.api;

import com.jesussoto.android.popularmovies.api.model.UserReview;
import com.jesussoto.android.popularmovies.api.model.Video;
import com.jesussoto.android.popularmovies.api.response.MultipleResultResponse;
import com.jesussoto.android.popularmovies.api.response.PagedResultResponse;
import com.jesussoto.android.popularmovies.db.entity.Movie;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {

    /**
     * Paths for TheMovieDB web service.
     */
    String PATH_MOVIE_POPULAR = "movie/popular";
    String PATH_MOVIE_TOP_RATED = "movie/top_rated";
    String PATH_MOVIE_VIDEOS = "movie/{movie_id}/videos";
    String PATH_MOVIE_REVIEWS = "movie/{movie_id}/reviews";
    String PATH_POSTER_SIZE = "w185";
    String PATH_BACKDROP_SIZE = "w500";

    /**
     * Parameters for the web service.
     */
    String PARAM_API_KEY = "api_key";
    String PARAM_PAGE = "page";
    String PARAM_MOVIE_ID = "movie_id";

    @GET(PATH_MOVIE_POPULAR)
    Single<PagedResultResponse<Movie>> getPopularMovies(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_TOP_RATED)
    Single<PagedResultResponse<Movie>> getTopRatedMovies(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_POPULAR)
    Call<PagedResultResponse<Movie>> getPopularMoviesCall(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_TOP_RATED)
    Call<PagedResultResponse<Movie>> getTopRatedMoviesCall(
            @Query(PARAM_PAGE) int page);

    @GET(PATH_MOVIE_REVIEWS)
    Single<PagedResultResponse<UserReview>> getReviews(
            @Path(PARAM_MOVIE_ID) long movieId);

    @GET(PATH_MOVIE_VIDEOS)
    Single<MultipleResultResponse<Video>> getVideos(
            @Path(PARAM_MOVIE_ID) long movieId);
}

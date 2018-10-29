package com.jesussoto.android.popularmovies.repository;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.api.model.UserReview;
import com.jesussoto.android.popularmovies.api.model.Video;
import com.jesussoto.android.popularmovies.api.response.MultipleResultResponse;
import com.jesussoto.android.popularmovies.api.response.PagedResultResponse;
import com.jesussoto.android.popularmovies.db.dao.MovieDao;
import com.jesussoto.android.popularmovies.db.entity.Movie;
import com.jesussoto.android.popularmovies.movies.MoviesListing;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class MoviesRepository {

    /**
     * Default page size by TheMovieDB API.
     */
    private static final int PAGE_SIZE = 20;

    // Web service to fetch the data from.
    private WebService mService;

    // Access object to deal with the database.
    private MovieDao mMovieDao;

    @Inject
    public MoviesRepository(@NonNull WebService service, @NonNull MovieDao movieDao) {
        mService = service;
        mMovieDao = movieDao;
    }

    /**
     * Builds paged popular movies
     *
     * @return {@link MoviesListing} encapsulating all information about popular movies stream.
     */
    public MoviesListing getPopularMoviesListing() {
        PopularMoviesDataSourceFactory factory = new PopularMoviesDataSourceFactory(mService);
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(2 * PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        LiveData<PagedList<Movie>> pagedList = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setFetchExecutor(Executors.newFixedThreadPool(3))
                .build();

        return new MoviesListing(
                pagedList,

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getNetworkState),

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getInitialLoading),

                () -> {
                    PopularMoviesPageKeyedDataSource dataSource = factory.getSourceLiveData().getValue();
                    if (dataSource != null) {
                        dataSource.retry();
                    }
                }
        );
    }

    /**
     * Builds paged top-rated movies
     *
     * @return {@link MoviesListing} encapsulating all information about top-rated movies stream.
     */
    public MoviesListing getTopRatedMoviesListing() {
        TopRatedMoviesDataSourceFactory factory = new TopRatedMoviesDataSourceFactory(mService);
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(2 * PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        LiveData<PagedList<Movie>> pagedList = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setFetchExecutor(Executors.newFixedThreadPool(3))
                .build();

        return new MoviesListing(
                pagedList,

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getNetworkState),

                Transformations.switchMap(factory.getSourceLiveData(),
                        AbstractMoviesPageKeyedDataSource::getInitialLoading),

                () -> {
                    TopRatedMoviesPageKeyedDataSource dataSource = factory.getSourceLiveData().getValue();
                    if (dataSource != null) {
                        dataSource.retry();
                    }
                }
        );
    }

    /**
     * Builds paged top-rated movies
     *
     * @return {@link MoviesListing} encapsulating all information about top-rated movies stream.
     */
    public MoviesListing getFavoriteMoviesListing() {
        DataSource.Factory<Integer, Movie> factory = mMovieDao.getFavoriteMovies();
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(2 * PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        LiveData<PagedList<Movie>> pagedList = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setFetchExecutor(Executors.newFixedThreadPool(3))
                .build();

        // Dummy network state since its not required for database source.
        MutableLiveData<Resource.Status> networkState = new MutableLiveData<>();
        networkState.setValue(null);

        // Force the initial state to be always success when loading from the local database.
        MutableLiveData<Resource.Status> initialLoadState = new MutableLiveData<>();
        initialLoadState.setValue(Resource.Status.SUCCESS);

        return new MoviesListing(pagedList, networkState, initialLoadState, null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public void getReviewsByMovieId(
            long movieId, @NonNull MutableLiveData<Resource<List<UserReview>>> notifyLiveData) {

        mService.getReviews(movieId)
                .doOnSubscribe(__ -> notifyLiveData.postValue(Resource.loading(null)))
                .map(PagedResultResponse::getResults)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(
                    result -> notifyLiveData.postValue(Resource.success(result)),
                    throwable -> notifyLiveData.postValue(Resource.error(throwable, null))
                );
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public void getVideosByMovieId(
            long movieId, @NonNull MutableLiveData<Resource<List<Video>>> notifyLiveData) {

        mService.getVideos(movieId)
                .doOnSubscribe(__ -> notifyLiveData.postValue(Resource.loading(null)))
                .map(MultipleResultResponse::getResults)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .subscribe(
                        result -> notifyLiveData.postValue(Resource.success(result)),
                        throwable -> notifyLiveData.postValue(Resource.error(throwable, null))
                );
    }

    public Completable markAsFavorite(@NonNull Movie movie) {
        return Completable.fromAction(() -> mMovieDao.insert(movie));
    }

    public Completable unmarkAsFavorite(@NonNull Movie movie) {
        return Completable.fromAction(() -> mMovieDao.delete(movie));
    }

    @SuppressWarnings("Convert2MethodRef")
    public LiveData<Boolean> isFavorite(@NonNull Movie movie) {
        return Transformations.map(
                mMovieDao.getMovieById(movie.getId()),
                (Movie result) -> result != null);
    }
}


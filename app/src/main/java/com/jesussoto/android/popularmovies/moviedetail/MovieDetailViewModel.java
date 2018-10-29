package com.jesussoto.android.popularmovies.moviedetail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jesussoto.android.popularmovies.R;
import com.jesussoto.android.popularmovies.api.Resource;
import com.jesussoto.android.popularmovies.api.model.UserReview;
import com.jesussoto.android.popularmovies.api.model.Video;
import com.jesussoto.android.popularmovies.db.entity.Movie;
import com.jesussoto.android.popularmovies.repository.MoviesRepository;
import com.jesussoto.android.popularmovies.util.SingleLiveEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

public class MovieDetailViewModel extends ViewModel {

    private Movie mMovie;

    private MutableLiveData<Movie> mMovieLiveData;

    private MoviesRepository mRepository;

    private MutableLiveData<Resource<List<UserReview>>> mUserReviewsData;

    private MutableLiveData<Resource<List<Video>>> mVideosData;

    private LiveData<Boolean> mIsFavoriteData;

    private SingleLiveEvent<Integer> mSnackbarText;

    @Inject
    public MovieDetailViewModel(MoviesRepository repository) {
        mRepository = repository;
        mMovieLiveData = new MutableLiveData<>();
        mUserReviewsData = new MutableLiveData<>();
        mVideosData = new MutableLiveData<>();
        mSnackbarText = new SingleLiveEvent<>();
    }

    public void setupWithMovie(Movie movie) {
        if (mMovie == null) {
            Log.d(getClass().getSimpleName(), "setupWithMovie: SETTING UP!!");
            mMovie = movie;
            mMovieLiveData.setValue(movie);
            loadVideos();
            loadUserReviews();
        }
    }

    private void loadUserReviews() {
        mRepository.getReviewsByMovieId(mMovie.getId(), mUserReviewsData);
    }

    private void loadVideos() {
        mRepository.getVideosByMovieId(mMovie.getId(), mVideosData);
    }

    public LiveData<Movie> getMovie() {
        return mMovieLiveData;
    }

    public LiveData<MovieReviewsUiModel> getUserReviews() {
        return Transformations.map(mUserReviewsData, this::constructReviewsUiModel);
    }

    public LiveData<MovieVideosUiModel> getVideos() {
        return Transformations.map(mVideosData, this::constructVideosUiModel);
    }

    @NonNull
    public LiveData<Boolean> getIsFavorite() {
        if (mIsFavoriteData == null) {
            mIsFavoriteData = mRepository.isFavorite(mMovie);
        }
        return mIsFavoriteData;
    }

    public SingleLiveEvent<Integer> getSnackbarText() {
        return mSnackbarText;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public void toggleFavorite() {
        if (mIsFavoriteData.getValue() == null || !mIsFavoriteData.getValue()) {
            mRepository.markAsFavorite(mMovie)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> mSnackbarText.postValue(R.string.msg_added_to_favorites));
        } else {
            mRepository.unmarkAsFavorite(mMovie)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> mSnackbarText.postValue(R.string.msg_removed_from_favorites));
        }
    }

    public void retryVideos() {
        loadVideos();
    }

    public void retryReviews() {
        loadUserReviews();
    }

    private MovieVideosUiModel constructVideosUiModel(Resource<List<Video>> videosResource) {
        Resource.Status status = videosResource.getStatus();
        List<Video> videos = videosResource.getData();
        boolean isLoadingVisible = status == Resource.Status.LOADING;
        boolean isEmpty = status == Resource.Status.SUCCESS && (videos == null || videos.isEmpty());
        Integer emptyMsgResId = null;

        if (status == Resource.Status.ERROR) {
            emptyMsgResId = R.string.error_connection;
        } else if (isEmpty) {
            emptyMsgResId = R.string.empty_videos;
        }

        boolean isEmptyViewVisible = status == Resource.Status.ERROR || isEmpty;
        boolean isRetryVisible = status == Resource.Status.ERROR;

        return new MovieVideosUiModel(videos, isLoadingVisible, isRetryVisible,
                isEmptyViewVisible, emptyMsgResId);
    }

    private MovieReviewsUiModel constructReviewsUiModel(Resource<List<UserReview>> reviewsResource) {
        Resource.Status status = reviewsResource.getStatus();
        List<UserReview> reviews = reviewsResource.getData();
        boolean isLoadingVisible = status == Resource.Status.LOADING;
        boolean isEmpty = status == Resource.Status.SUCCESS && (reviews == null || reviews.isEmpty());
        Integer emptyMsgResId = null;

        if (status == Resource.Status.ERROR) {
            emptyMsgResId = R.string.error_connection;
        } else if (isEmpty) {
            emptyMsgResId = R.string.empty_reviews;
        }

        boolean isEmptyViewVisible = status == Resource.Status.ERROR || isEmpty;
        boolean isRetryVisible = status == Resource.Status.ERROR;

        return new MovieReviewsUiModel(reviews, isLoadingVisible, isRetryVisible,
                isEmptyViewVisible, emptyMsgResId);
    }
}

package com.jesussoto.android.popularmovies.di;

import com.jesussoto.android.popularmovies.di.scope.ActivityScope;
import com.jesussoto.android.popularmovies.moviedetail.MovieDetailActivity;
import com.jesussoto.android.popularmovies.moviedetail.MovieDetailModule;
import com.jesussoto.android.popularmovies.movies.MoviesActivity;
import com.jesussoto.android.popularmovies.movies.MoviesModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = MoviesModule.class)
    abstract MoviesActivity contributeMoviesActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = MovieDetailModule.class)
    abstract MovieDetailActivity contributeMovieDetailActivity();
}

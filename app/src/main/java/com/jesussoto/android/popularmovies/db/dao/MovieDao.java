package com.jesussoto.android.popularmovies.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.jesussoto.android.popularmovies.db.entity.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie WHERE id = :movieId")
    LiveData<Movie> getMovieById(long movieId);

    @Query("SELECT * FROM movie ORDER BY title")
    DataSource.Factory<Integer, Movie> getFavoriteMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

}

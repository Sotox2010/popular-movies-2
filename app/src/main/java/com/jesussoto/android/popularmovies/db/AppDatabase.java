package com.jesussoto.android.popularmovies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.jesussoto.android.popularmovies.db.converter.DateConverter;
import com.jesussoto.android.popularmovies.db.dao.MovieDao;
import com.jesussoto.android.popularmovies.db.entity.Movie;

@Database(
    entities = {
        Movie.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({
    DateConverter.class
})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "pop_movies.db";

    public abstract MovieDao movieDao();

}

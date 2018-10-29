package com.jesussoto.android.popularmovies.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jesussoto.android.popularmovies.BuildConfig;
import com.jesussoto.android.popularmovies.api.AuthInterceptor;
import com.jesussoto.android.popularmovies.api.WebService;
import com.jesussoto.android.popularmovies.api.WebServiceUtils;
import com.jesussoto.android.popularmovies.db.AppDatabase;
import com.jesussoto.android.popularmovies.db.dao.MovieDao;
import com.jesussoto.android.popularmovies.di.qualifier.ApplicationContext;
import com.squareup.picasso.Picasso;

import java.util.Date;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Singleton
    @Provides
    @ApplicationContext
    @NonNull
    public Context provideApplicationContext(Application app) {
        return app.getApplicationContext();
    }

    @Provides
    @NonNull
    public OkHttpClient.Builder provideOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor());

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        return builder;
    }

    @Singleton
    @Provides
    @NonNull
    public WebService provideWebService(OkHttpClient.Builder clientBuilder) {
        clientBuilder.addInterceptor(new AuthInterceptor());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new WebServiceUtils.JsonDateDeserializer())
                .create();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.TMDB_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create(WebService.class);
    }

    @Singleton
    @Provides
    @NonNull
    public Picasso providePicasso(@ApplicationContext Context appContext,
                                  OkHttpClient.Builder clientBuilder) {

        return new Picasso.Builder(appContext)
                .downloader(new OkHttp3Downloader(clientBuilder.build()))
                .build();
    }

    @Singleton
    @Provides
    @NonNull
    public AppDatabase provideDatabase(@ApplicationContext Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    @NonNull
    public MovieDao provideMovieDao(AppDatabase database) {
        return database.movieDao();
    }
}

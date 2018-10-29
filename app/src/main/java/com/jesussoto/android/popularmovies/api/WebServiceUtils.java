package com.jesussoto.android.popularmovies.api;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jesussoto.android.popularmovies.BuildConfig;
import com.jesussoto.android.popularmovies.util.DateTimeUtils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebServiceUtils {

    private WebServiceUtils() { }

    @NonNull
    public static Uri buildMoviePosterUri(@NonNull String moviePosterPath) {
        return Uri.parse(BuildConfig.TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(WebService.PATH_POSTER_SIZE)
                .appendPath(moviePosterPath.substring(1))
                .build();
    }

    @NonNull
    public static Uri buildMovieBackdropUri(@NonNull String movieBackdropPath) {
        return Uri.parse(BuildConfig.TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(WebService.PATH_BACKDROP_SIZE)
                .appendPath(movieBackdropPath.substring(1))
                .build();
    }


    public static Uri buildYoutubeThumbUri(String videoKey) {
        return Uri.parse("https://img.youtube.com/vi").buildUpon()
                .appendPath(videoKey)
                .appendPath("default.jpg")
                .build();
    }

    public static class JsonDateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            String dateString = jsonElement.getAsString();
            if (dateString == null) {
                return null;
            }

            try {
                return DateTimeUtils.parseServerDate(jsonElement.getAsString());
            } catch (Throwable t) {
                return null;
            }
        }
    }
}

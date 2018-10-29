package com.jesussoto.android.popularmovies.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    /**
     * Pre-defined date formats to use across the app.
     */
    private static final DateFormat SERVER_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.US);

    private static final DateFormat DATABASE_DATETIME_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);

    private DateTimeUtils() { }

    public static Date parseServerDate(@NonNull String serverDate) throws ParseException {
        return SERVER_DATE_FORMAT.parse(serverDate);
    }

    public static Date parseDatabaseDate(@NonNull String dbDate) throws ParseException {
        return DATABASE_DATETIME_FORMAT.parse(dbDate);
    }

    public static String formatDatabaseDate(@NonNull Date serverDate) {
        return SERVER_DATE_FORMAT.format(serverDate);
    }
}

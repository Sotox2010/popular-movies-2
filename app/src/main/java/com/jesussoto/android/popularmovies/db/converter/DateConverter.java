package com.jesussoto.android.popularmovies.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.jesussoto.android.popularmovies.util.DateTimeUtils;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public Date fromTimestamp(String timestamp) {
        if (timestamp == null) {
            return null;
        }

        Date result = null;

        try {
            result = DateTimeUtils.parseDatabaseDate(timestamp);
        } catch (Throwable t) {
            // ignored.
        }

        if (result != null) {
            return result;
        }

        try {
            result = DateTimeUtils.parseServerDate(timestamp);
        } catch (Throwable t) {
            // ignored.
        }

        return result;
    }

    @TypeConverter
    public String dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return DateTimeUtils.formatDatabaseDate(date);
        }
    }
}

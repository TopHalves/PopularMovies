package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *  Utility methods for converting the date returned from movie db.
 */

public final class MovieDateUtils {

    /**
     * yearFromMilliseconds returns the year part of a milliseconds from epoch time.
     * @param time time in milliseconds from the epoch.
     * @return the year.
     */
    public static int yearFromMilliseconds(long time){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return calendar.get(Calendar.YEAR);
    }

    /**
     * Converts the text date used as release date to a Date object.
     * @param textDate text version of a date used by the movie db.
     * @return a Date object representation of the text data.
     */
    @Nullable
    static Date dateFromDBTextDate(Context context, String textDate){
        DateFormat df = new SimpleDateFormat(
                context.getString(R.string.format_date_database), Locale.US);
        Date parsedDate;
        try {
            parsedDate = df.parse(textDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return parsedDate;
    }

    /**
     * Converts a date into milliseconds from the epoch.
     * @param date the date to convert.
     * @return the milliseconds from the epoch.
     */
    static long millisecondTimeForDate(Date date){
        return date.getTime();
    }

}


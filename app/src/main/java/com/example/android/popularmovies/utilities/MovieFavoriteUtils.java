package com.example.android.popularmovies.utilities;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.MovieContract;

/*
 * Class of utility methods for getting, setting and un-setting favorite movies via the
 * movie content provider
 */
public class MovieFavoriteUtils {

    /* The favorite table only contains movie ids */
    private static final String[] FAVORITE_PROJECTION = {
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID
    };

    /**
     * Determine if a movie is a favorite.
     *
     * @param context the context to use
     * @param movieId the movie id
     * @return true if the movie id a favorite
     */
    public static boolean isFavoriteMovie(@NonNull final Context context, int movieId) {
        /* Using the favorite query uri that includes the movie id */
        Uri favouriteByIdUri = MovieContract.FavoriteEntry.buildFavoriteUriWithId(movieId);

        boolean isFavorite = false;

        Cursor cursor = context.getContentResolver().query(
                favouriteByIdUri,
                FAVORITE_PROJECTION,
                null,
                null,
                null);

        /* If the favorite table has this movie id then it is a favorite */
        if (cursor != null && cursor.getCount() == 1) {
            isFavorite = true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return isFavorite;
    }

    /**
     * Sets/unsets a movie as a favorite by adding/removing it to the favorites table. Does nothing
     * if the calling would cause no change.
     *
     * @param context the context to use
     * @param movieId the movie id to set or unset as favorite
     * @param favorite if true sets as favorite, if false will unset as favorite
     */
    public static void setFavoriteMovie(@NonNull final Context context, int movieId, boolean favorite) {
        boolean isCurrentlyFavorite = isFavoriteMovie(context, movieId);

        if ( (favorite && isCurrentlyFavorite) || (!favorite && !isCurrentlyFavorite) ) {
            /* Nothing to do - state already as requested */
            return;
        }

        if (favorite) {
            addFavorite(context, movieId);
        } else {
            removeFavorite(context, movieId);
        }
    }

    /**
     * Adds a movie id to the favorites table
     *
     * @param context the context to use
     * @param movieId the movie id to add to the favorites table
     */
    private static void addFavorite(@NonNull final Context context, int movieId) {
        Uri uri = MovieContract.FavoriteEntry.CONTENT_URI;

        ContentValues value = new ContentValues();

        value.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieId);

        context.getContentResolver().insert(uri, value);
    }

    /**
     * Removes a favorite from the favorites table.
     *
     * @param context the context to use.
     * @param movieId the movie id to remove from the favorites table
     */
    private static void removeFavorite(@NonNull final Context context, int movieId) {
        Uri uri = MovieContract.FavoriteEntry.buildFavoriteUriWithId(movieId);

        context.getContentResolver().delete(uri, null, null);
    }
}

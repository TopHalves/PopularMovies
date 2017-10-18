package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;

/*
 * Utility class for querying the current sort preferences
 */
public class MoviePrefUtils {

    /**
     * Get the current sort order from the shared preferences
     * @param context the current context
     * @return the current sort order preference
     */
    public static String currentSortOrder(@NonNull final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the preferred sort order from the preferences
        return sharedPreferences.getString(
                context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    /**
     * Get the appropriate MovieContentProvider Uri for the current sort order
     * @param context the current context
     * @return the uri for the current sort order
     */
    public static Uri queryUriForCurrentSortOption(@NonNull final Context context) {

        Uri queryUri;
        String sortOption = currentSortOrder(context);

        // Get the appropriate query URI for the sort type
        if (sortOption.equals(context.getString(R.string.pref_sort_popular))) {
            queryUri = MovieContract.PopularEntry.CONTENT_URI;
        }
        else if (sortOption.equals(context.getString(R.string.pref_sort_top_rated))) {
            queryUri = MovieContract.TopRatedEntry.CONTENT_URI;
        }
        else {
            queryUri = MovieContract.FavoriteEntry.CONTENT_URI;
        }

        return queryUri;
    }
}

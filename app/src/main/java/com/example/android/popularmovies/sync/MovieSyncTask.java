package com.example.android.popularmovies.sync;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.MovieDbJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

class MovieSyncTask {

    private static final String TAG = MovieSyncTask.class.getCanonicalName();

    /**
     * Perform the synchronization of the local movie data from the remote movie db.
     *
     * @param context the current context
     * @param syncArgs the target sync arguments
     */
    static void syncMovieData(@NonNull final Context context,
                                     @NonNull final MovieSyncArgs syncArgs){
        String movieQuery = syncArgs.getTargetRemoteURL();
        String targetTable = syncArgs.getTargetDatabaseUri();

        syncMovieData(context, movieQuery, targetTable);
    }

    /**
     * Perform the synchronization of the local movie data from the remote movie db. Overload
     * allows for String arguments.
     *
     * @param context the current context
     * @param movieQuery the remote target Url
     * @param targetTable the local provider query for the target
     */
    synchronized static void syncMovieData(@NonNull final Context context,
                                                  @NonNull final String movieQuery,
                                                  @NonNull final String targetTable){
        try {
            URL movieQueryUrl = new URL(movieQuery);
            Uri targetTableUri = Uri.parse(targetTable);

            ContentResolver contentProvider = context.getContentResolver();

            String result = NetworkUtils.getResponseFromHttpUrl(movieQueryUrl);

            ArrayList<String> results = MovieDbJsonUtils.arrayDataForMovieList(result);

            /*
             * Get all the movie entries for the retrieved list - any duplicated entries will be
             * silently overwritten on insert.
             */
            ArrayList<ContentValues> allMovies = MovieDbJsonUtils.movieContentArrayFromStringArray(
                    context, results);

            /* Add all the movie data to the movies table via the content provider */
            contentProvider.bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                    allMovies.toArray(new ContentValues[allMovies.size()]));

            /* Get all the reviews for all previously retrieved the movies */
            ArrayList<ContentValues> allReviews = MovieDbJsonUtils.reviewContentArrayFromStringArray(
                    context, results);

            /* Add all the reviews to the database */
            contentProvider.bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,
                    allReviews.toArray(new ContentValues[allReviews.size()]));

            /* Get all the trailers for all previously retrieved the movies */
            ArrayList<ContentValues> allTrailers = MovieDbJsonUtils.trailerContentArrayFromStringArray(
                    context, results);

            /* Add all the trailers to the database */
            contentProvider.bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,
                    allTrailers.toArray(new ContentValues[allReviews.size()]));

            /* Now get just a list of IDs for the target list */
            ArrayList<ContentValues> movieIdList = MovieDbJsonUtils.movieIdListFromJson(result);

            /* Delete all the existing entries for the target list type via the content provider */
            contentProvider.delete(targetTableUri, null, null);

            /* Add all the movie ids to the appropriate table */
            contentProvider.bulkInsert(targetTableUri,
                    movieIdList.toArray(new ContentValues[movieIdList.size()]));
        } catch (Exception e) {
            Log.e(TAG, "Unable to sync: " + e.toString());
        }
    }
}
